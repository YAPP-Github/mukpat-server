package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.repository.ParticipantQuerydslRepository
import com.yapp.muckpot.email.EmailService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ParticipantService(
    private val participantQuerydslRepository: ParticipantQuerydslRepository,
    private val emailService: EmailService
) {
    @Transactional
    fun sendEmailToParticipants(board: Board, mailTitle: String, mailBody: String) {
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
    }
}
