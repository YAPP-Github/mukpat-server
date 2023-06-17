package com.yapp.muckpot.domains.board.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.domains.board.dto.ParticipantReadResponse
import com.yapp.muckpot.domains.board.dto.QParticipantReadResponse
import com.yapp.muckpot.domains.board.entity.QParticipant.participant
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ParticipantQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    @Transactional(readOnly = true)
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
}
