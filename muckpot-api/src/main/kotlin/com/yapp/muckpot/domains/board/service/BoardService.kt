package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.common.SecurityContextHolderUtil
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.ParticipantRepository
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
    private val participantRepository: ParticipantRepository
) {
    @Transactional
    fun saveBoard(request: MuckpotCreateRequest): Long? {
        // TODO 먹팟 등록 시 같은 회사 인원에게 메일 전송
        val user = userRepository.findByIdOrNull(SecurityContextHolderUtil.getCurrentUserId())
            ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
        val board = boardRepository.save(request.toBoard(user))
        participantRepository.save(Participant(user, board))
        return board.id
    }
}
