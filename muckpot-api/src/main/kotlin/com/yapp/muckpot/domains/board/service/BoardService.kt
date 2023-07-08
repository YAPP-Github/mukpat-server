package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.common.dto.CursorPaginationRequest
import com.yapp.muckpot.common.dto.CursorPaginationResponse
import com.yapp.muckpot.common.redisson.DistributedLock
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequestV1
import com.yapp.muckpot.domains.board.controller.dto.MuckpotDetailResponse
import com.yapp.muckpot.domains.board.controller.dto.MuckpotReadResponse
import com.yapp.muckpot.domains.board.controller.dto.MuckpotUpdateRequest
import com.yapp.muckpot.domains.board.entity.City
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.entity.Province
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.domains.board.exception.ParticipantErrorCode
import com.yapp.muckpot.domains.board.repository.BoardQuerydslRepository
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.CityRepository
import com.yapp.muckpot.domains.board.repository.ParticipantQuerydslRepository
import com.yapp.muckpot.domains.board.repository.ParticipantRepository
import com.yapp.muckpot.domains.board.repository.ProvinceRepository
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.email.EmailService
import com.yapp.muckpot.email.EmailTemplate
import com.yapp.muckpot.exception.MuckPotException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardService(
    private val userRepository: MuckPotUserRepository,
    private val boardRepository: BoardRepository,
    private val boardQuerydslRepository: BoardQuerydslRepository,
    private val participantRepository: ParticipantRepository,
    private val participantQuerydslRepository: ParticipantQuerydslRepository,
    private val emailService: EmailService,
    private val cityRepository: CityRepository,
    private val provinceRepository: ProvinceRepository
) {
    fun getMuckpotProvince(depth1: String, depth2: String): Province {
        val city = cityRepository.findByName(depth1) ?: cityRepository.save(City(depth1))
        return provinceRepository.findByName(depth2) ?: provinceRepository.save(Province(depth2, city))
    }

    @Transactional
    fun saveBoard(userId: Long, request: MuckpotCreateRequest): Long? {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
        val province = getMuckpotProvince(request.region_1depth_name, request.region_2depth_name)
        val board = boardRepository.save(request.toBoard(user, province))
        participantRepository.save(Participant(user, board))
        return board.id
    }

    @Transactional(readOnly = true)
    fun findAllMuckpot(request: CursorPaginationRequest): CursorPaginationResponse<MuckpotReadResponse> {
        val allBoard = boardQuerydslRepository.findAllWithPagination(request.lastId, request.countPerScroll)
        val boardIds = allBoard.map { it.id }
        val participantsByBoardId = participantQuerydslRepository.findByBoardIds(boardIds).groupBy { it.boardId }
        val responseList = allBoard.map { MuckpotReadResponse.of(it, participantsByBoardId.getOrDefault(it.id, emptyList())) }
        if (responseList.isNotEmpty()) {
            return CursorPaginationResponse(responseList, responseList.last().boardId)
        }
        return CursorPaginationResponse(responseList)
    }

    @Transactional
    fun findBoardDetailAndVisit(boardId: Long, loginUserInfo: UserResponse?): MuckpotDetailResponse {
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            if (board.user.id != loginUserInfo?.userId) {
                board.visit()
            }
            val userAge: Int? = loginUserInfo?.let { userRepository.findByIdOrNull(it.userId)?.getAge() }

            return MuckpotDetailResponse.of(
                board = board,
                participants = participantQuerydslRepository.findByBoardIds(listOf(boardId)),
                prevId = boardQuerydslRepository.findPrevId(boardId),
                nextId = boardQuerydslRepository.findNextId(boardId),
                userAge = userAge
            ).apply {
                sortParticipantsByLoginUser(loginUserInfo)
            }
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @Transactional
    fun updateBoardAndSendEmail(userId: Long, boardId: Long, request: MuckpotUpdateRequest) {
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            if (board.isNotMyBoard(userId)) {
                throw MuckPotException(BoardErrorCode.BOARD_UNAUTHORIZED)
            }
            // Update 이전에 수행되어야 함.
            val mailTitle = EmailTemplate.BOARD_UPDATE_EMAIL.formatSubject(board.title)
            val mailBody = EmailTemplate.BOARD_UPDATE_EMAIL.formatBody(
                board.title,
                request.createBoardUpdateMailBody(board)
            )
            request.updateBoard(board)
            // TODO MQ 적용
            participantQuerydslRepository.findParticipantEmails(board).forEach { email ->
                if (board.user.email != email) {
                    emailService.sendMail(
                        subject = mailTitle,
                        body = mailBody,
                        to = email
                    )
                }
            }
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @DistributedLock(lockName = "joinLock", identifier = "boardId")
    fun joinBoard(userId: Long, boardId: Long) {
        boardRepository.findByIdOrNull(boardId)?.let {
            val user = userRepository.findByIdOrNull(userId) ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
            val participant = participantRepository.findByUserAndBoard(user, it)
            if (participant != null) throw MuckPotException(ParticipantErrorCode.ALREADY_JOIN)
            it.join(user.getAge())
            participantRepository.save(Participant(user, it))
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @Transactional
    fun deleteBoard(userId: Long, boardId: Long) {
        // TODO 먹팟 삭제 시 참여 인원에게 메일 전송
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            if (board.isNotMyBoard(userId)) {
                throw MuckPotException(BoardErrorCode.BOARD_UNAUTHORIZED)
            }
            participantRepository.deleteByBoard(board)
            boardRepository.delete(board)
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

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
    fun cancelJoin(userId: Long, boardId: Long) {
        // TODO 먹팟 참가 신청 취소 시 참여 인원에게 메일 전송 기획 논의
        boardRepository.findByIdOrNull(boardId)?.let { board ->
            val user = userRepository.findByIdOrNull(userId)
                ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
            val participant = participantRepository.findByUserAndBoard(user, board)
                ?: throw MuckPotException(ParticipantErrorCode.PARTICIPANT_NOT_FOUND)
            if (board.user.id == userId) throw MuckPotException(ParticipantErrorCode.WRITER_MUST_JOIN)
            participantRepository.delete(participant)
            board.cancelJoin()
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }

    @Transactional
    fun saveBoardV1(userId: Long, request: MuckpotCreateRequestV1): Long? {
        // TODO 먹팟 등록 시 같은 회사 인원에게 메일 전송
        val user = userRepository.findByIdOrNull(userId)
            ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
        val board = boardRepository.save(request.toBoard(user))
        participantRepository.save(Participant(user, board))
        return board.id
    }
}
