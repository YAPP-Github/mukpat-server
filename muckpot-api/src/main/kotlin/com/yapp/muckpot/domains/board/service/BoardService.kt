package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.common.dto.CursorPaginationRequest
import com.yapp.muckpot.common.dto.CursorPaginationResponse
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotDetailResponse
import com.yapp.muckpot.domains.board.controller.dto.MuckpotReadResponse
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.domains.board.repository.BoardQuerydslRepository
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.ParticipantQuerydslRepository
import com.yapp.muckpot.domains.board.repository.ParticipantRepository
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
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
    private val participantQuerydslRepository: ParticipantQuerydslRepository
) {
    @Transactional
    fun saveBoard(userId: Long, request: MuckpotCreateRequest): Long? {
        // TODO 먹팟 등록 시 같은 회사 인원에게 메일 전송
        val user = userRepository.findByIdOrNull(userId)
            ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
        val board = boardRepository.save(request.toBoard(user))
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
        boardRepository.findByIdOrNull(boardId)?.let {
            it.visit()
            return MuckpotDetailResponse.of(it, participantQuerydslRepository.findByBoardIds(listOf(boardId))).apply {
                sortParticipantsByLoginUser(loginUserInfo)
            }
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }
}
