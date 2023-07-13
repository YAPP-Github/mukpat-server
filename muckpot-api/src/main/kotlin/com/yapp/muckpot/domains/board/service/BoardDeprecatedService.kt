package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.domains.board.controller.dto.deprecated.MuckpotCreateRequestV1
import com.yapp.muckpot.domains.board.controller.dto.deprecated.MuckpotUpdateRequestV1
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.ParticipantRepository
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.email.EmailTemplate
import com.yapp.muckpot.exception.MuckPotException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Deprecated("V2 배포 후 제거")
class BoardDeprecatedService(
    private val userRepository: MuckPotUserRepository,
    private val boardRepository: BoardRepository,
    private val participantRepository: ParticipantRepository,
    private val participantService: ParticipantService
) {
    @Transactional
    fun saveBoardV1(userId: Long, request: MuckpotCreateRequestV1): Long? {
        // TODO 먹팟 등록 시 같은 회사 인원에게 메일 전송
        val user = userRepository.findByIdOrNull(userId)
            ?: throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
        val board = boardRepository.save(request.toBoard(user))
        participantRepository.save(Participant(user, board))
        return board.id
    }

    @Transactional
    fun updateBoardAndSendEmailV1(userId: Long, boardId: Long, request: MuckpotUpdateRequestV1) {
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
            participantService.sendEmailToParticipantsWithoutWriter(
                board = board,
                mailTitle = mailTitle,
                mailBody = mailBody
            )
        } ?: run {
            throw MuckPotException(BoardErrorCode.BOARD_NOT_FOUND)
        }
    }
}
