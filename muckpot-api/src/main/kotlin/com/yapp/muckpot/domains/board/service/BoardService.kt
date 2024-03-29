package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.common.dto.CursorPaginationResponse
import com.yapp.muckpot.common.redisson.DistributedLock
import com.yapp.muckpot.domains.board.controller.converter.RegionConverter
import com.yapp.muckpot.domains.board.controller.dto.AllMuckpotGetRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotDetailResponse
import com.yapp.muckpot.domains.board.controller.dto.MuckpotReadResponse
import com.yapp.muckpot.domains.board.controller.dto.MuckpotUpdateDetailResponse
import com.yapp.muckpot.domains.board.controller.dto.MuckpotUpdateRequest
import com.yapp.muckpot.domains.board.controller.dto.RegionFilterRequest
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.domains.board.exception.ParticipantErrorCode
import com.yapp.muckpot.domains.board.repository.BoardQuerydslRepository
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.ParticipantQuerydslRepository
import com.yapp.muckpot.domains.board.repository.ParticipantRepository
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.email.EmailSendEvent
import com.yapp.muckpot.email.EmailTemplate
import com.yapp.muckpot.exception.MuckPotException
import com.yapp.muckpot.redis.constants.ALL_KEY
import com.yapp.muckpot.redis.constants.REGIONS_CACHE_NAME
import com.yapp.muckpot.redis.dto.MuckpotCityResponse
import com.yapp.muckpot.redis.dto.RegionResponse
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// TODO 수정, 삭제, 참여취소시 메일전송기능 Bulk 처리
@Service
class BoardService(
    private val userRepository: MuckPotUserRepository,
    private val boardRepository: BoardRepository,
    private val boardQuerydslRepository: BoardQuerydslRepository,
    private val participantRepository: ParticipantRepository,
    private val participantQuerydslRepository: ParticipantQuerydslRepository,
    private val provinceService: ProvinceService,
    private val eventPublisher: ApplicationEventPublisher
) {
    @CacheEvict(value = [REGIONS_CACHE_NAME], key = ALL_KEY)
    @Transactional
    fun saveBoard(userId: Long, request: MuckpotCreateRequest): Long? {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
        val province = provinceService.saveProvinceIfNot(request.region_1depth_name, request.region_2depth_name)
        val board = boardRepository.save(request.toBoard(user, province))
        participantRepository.save(Participant(user, board))
        return board.id
    }

    @Transactional(readOnly = true)
    fun findAllBoards(request: AllMuckpotGetRequest): CursorPaginationResponse<MuckpotReadResponse> {
        val allBoard = boardQuerydslRepository.findAllWithPaginationAndRegion(request.lastId, request.countPerScroll, request.cityId, request.provinceId)
        val boardIds = allBoard.map { it.id }
        val participantsByBoardId = participantQuerydslRepository.findByBoardIds(boardIds).groupBy { it.boardId }
        val responseList = allBoard.map { MuckpotReadResponse.of(it, participantsByBoardId.getOrDefault(it.id, emptyList())) }
        if (responseList.isNotEmpty()) {
            return CursorPaginationResponse(responseList, responseList.last().boardId)
        }
        return CursorPaginationResponse(responseList)
    }

    @Transactional
    fun findBoardDetailAndVisit(boardId: Long, loginUserInfo: UserResponse?, request: RegionFilterRequest): MuckpotDetailResponse {
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            if (board.user.id != loginUserInfo?.userId) {
                board.visit()
            }
            val userAge: Int? = loginUserInfo?.let { userRepository.findByIdOrNull(it.userId)?.getAge() }

            return MuckpotDetailResponse.of(
                board = board,
                participants = participantQuerydslRepository.findByBoardIds(listOf(boardId)),
                prevId = boardQuerydslRepository.findPrevId(boardId, request.cityId, request.provinceId),
                nextId = boardQuerydslRepository.findNextId(boardId, request.cityId, request.provinceId),
                userAge = userAge
            ).apply {
                sortParticipantsByLoginUser(loginUserInfo)
            }
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @CacheEvict(value = [REGIONS_CACHE_NAME], key = ALL_KEY)
    @Transactional
    fun updateBoardAndSendEmail(userId: Long, boardId: Long, request: MuckpotUpdateRequest) {
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            if (board.isNotMyBoard(userId)) {
                throw MuckPotException(BoardErrorCode.BOARD_UNAUTHORIZED)
            }
            if (board.isDone()) {
                throw MuckPotException(BoardErrorCode.DONE_BOARD_NOT_UPDATE)
            }
            // Update 이전에 수행되어야 함.
            val mailTitle = EmailTemplate.BOARD_UPDATE_EMAIL.formatSubject(board.title)
            val mailBody = EmailTemplate.BOARD_UPDATE_EMAIL.formatBody(
                board.title,
                request.createBoardUpdateMailBody(board)
            )
            val province = provinceService.saveProvinceIfNot(request.region_1depth_name, request.region_2depth_name)
            request.updateBoard(board, province)
            participantQuerydslRepository.findParticipantsEmailsExcept(board, board.user.email).forEach { email ->
                eventPublisher.publishEvent(
                    EmailSendEvent(
                        subject = mailTitle,
                        body = mailBody,
                        to = email
                    )
                )
            }
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @DistributedLock(lockName = "joinLock", identifier = "boardId")
    fun joinBoard(userId: Long, boardId: Long) {
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            val user = userRepository.findByIdOrNull(userId) ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
            val participant = participantRepository.findByUserAndBoard(user, board)
            if (participant != null) throw MuckPotException(ParticipantErrorCode.ALREADY_JOIN)
            board.join(user.getAge())
            if (board.isFull()) {
                provinceService.clearRegionsAll()
            }
            participantRepository.save(Participant(user, board))
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @CacheEvict(value = [REGIONS_CACHE_NAME], key = ALL_KEY)
    @Transactional
    fun deleteBoardAndSendEmail(userId: Long, boardId: Long) {
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            if (board.isNotMyBoard(userId)) {
                throw MuckPotException(BoardErrorCode.BOARD_UNAUTHORIZED)
            }
            // DELETE 이전에 수행되어야 함.
            val mailTitle = EmailTemplate.BOARD_DELETE_EMAIL.formatSubject(board.title)
            val mailBody = EmailTemplate.BOARD_DELETE_EMAIL.formatBody(board.title)
            participantQuerydslRepository.findParticipantsEmailsExcept(board, board.user.email).forEach { email ->
                eventPublisher.publishEvent(
                    EmailSendEvent(
                        subject = mailTitle,
                        body = mailBody,
                        to = email
                    )
                )
            }
            participantRepository.deleteByBoard(board)
            boardRepository.delete(board)
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @CacheEvict(value = [REGIONS_CACHE_NAME], key = ALL_KEY)
    @Transactional
    fun changeStatus(userId: Long, boardId: Long, changeStatus: MuckPotStatus) {
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            if (board.isNotMyBoard(userId)) {
                throw MuckPotException(BoardErrorCode.BOARD_UNAUTHORIZED)
            }
            board.changeStatus(changeStatus)
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @Transactional
    fun cancelJoinAndSendEmail(userId: Long, boardId: Long) {
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            val user = userRepository.findByIdOrNull(userId)
                ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
            val participant = participantRepository.findByUserAndBoard(user, board)
                ?: throw MuckPotException(ParticipantErrorCode.PARTICIPANT_NOT_FOUND)
            if (board.user.id == userId) throw MuckPotException(ParticipantErrorCode.WRITER_MUST_JOIN)
            // DELETE 이전에 수행되어야 함.
            val mailTitle = EmailTemplate.PARTICIPANT_CANCEL_EMAIL.formatSubject(user.nickName, board.title)
            val mailBody = EmailTemplate.PARTICIPANT_CANCEL_EMAIL.formatBody(user.nickName, board.title)
            participantQuerydslRepository.findParticipantsEmailsExcept(board, user.email).forEach { email ->
                eventPublisher.publishEvent(
                    EmailSendEvent(
                        subject = mailTitle,
                        body = mailBody,
                        to = email
                    )
                )
            }
            if (board.isFull()) {
                provinceService.clearRegionsAll()
            }
            participantRepository.delete(participant)
            board.cancelJoin()
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @Cacheable(value = [REGIONS_CACHE_NAME], key = ALL_KEY)
    @Transactional(readOnly = true)
    fun findAllRegions(): RegionResponse {
        val muckpotCityResponses: MutableList<MuckpotCityResponse> = mutableListOf()
        boardQuerydslRepository.findAllRegions().groupBy { it.city }
            .mapValues { (city, provinces) ->
                muckpotCityResponses.add(RegionConverter.convertToCityResponse(city, provinces))
            }
        return RegionResponse(muckpotCityResponses)
    }

    @Transactional(readOnly = true)
    fun findUpdateBoardDetail(boardId: Long, loginUserInfo: UserResponse?): MuckpotUpdateDetailResponse {
        boardQuerydslRepository.findByIdOrNullWithRegion(boardId)?.let { board ->
            val userAge: Int? = loginUserInfo?.let { userRepository.findByIdOrNull(it.userId)?.getAge() }
            return MuckpotUpdateDetailResponse.of(
                board = board,
                userAge = userAge
            )
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }
}
