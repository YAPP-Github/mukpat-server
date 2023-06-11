package com.yapp.muckpot.domains.board.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.domains.board.dto.ParticipantReadResponse
import com.yapp.muckpot.domains.board.dto.QParticipantReadResponse
import com.yapp.muckpot.domains.board.entity.QParticipant.participant
import org.springframework.stereotype.Repository

@Repository
class ParticipantQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findByBoardIds(boardIds: List<Long?>): List<ParticipantReadResponse> {
        return queryFactory.select(
            QParticipantReadResponse(
                participant.participantId.board.id,
                participant.participantId.user.id,
                participant.participantId.user.nickName,
                participant.participantId.user.mainCategory
            )
        )
            .from(participant)
            .where(participant.participantId.board.id.`in`(boardIds))
            .orderBy(participant.createdAt.asc())
            .fetch()
    }
}
