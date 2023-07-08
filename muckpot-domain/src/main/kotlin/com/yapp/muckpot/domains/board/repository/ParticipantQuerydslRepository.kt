package com.yapp.muckpot.domains.board.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.domains.board.dto.ParticipantReadResponse
import com.yapp.muckpot.domains.board.dto.QParticipantReadResponse
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.QParticipant.participant
import com.yapp.muckpot.domains.user.entity.QMuckPotUser.muckPotUser
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class ParticipantQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findByBoardIds(boardIds: List<Long?>): List<ParticipantReadResponse> {
        return queryFactory.select(
            QParticipantReadResponse(
                participant.board.id,
                participant.user.id,
                participant.user.nickName,
                participant.user.mainCategory
            )
        )
            .from(participant)
            .where(participant.board.id.`in`(boardIds))
            .orderBy(participant.createdAt.asc())
            .fetch()
    }

    fun findParticipantEmails(board: Board): List<String> {
        return queryFactory.select(
            muckPotUser.email
        )
            .from(participant)
            .innerJoin(participant.user, muckPotUser)
            .where(
                participant.board.eq(board)
            )
            .fetch()
    }
}
