package com.yapp.muckpot.domains.board.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.QBoard.board
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional(readOnly = true)
class BoardQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findAllWithPagination(lastId: Long?, countPerScroll: Long): List<Board> {
        return queryFactory.from(board)
            .select(board)
            .where(
                lessThanLastId(lastId)
            )
            .orderBy(board.createdAt.desc())
            .limit(countPerScroll)
            .fetch()
    }

    fun findPrevId(boardId: Long): Long? {
        return queryFactory.from(board)
            .select(board.id)
            .where(
                board.createdAt.gt(
                    JPAExpressions
                        .select(board.createdAt)
                        .from(board)
                        .where(board.id.eq(boardId))
                )
            )
            .orderBy(board.createdAt.asc())
            .limit(1)
            .fetchOne()
    }

    fun findNextId(boardId: Long): Long? {
        return queryFactory.from(board)
            .select(board.id)
            .where(
                board.createdAt.lt(
                    JPAExpressions
                        .select(board.createdAt)
                        .from(board)
                        .where(board.id.eq(boardId))
                )
            )
            .orderBy(board.createdAt.desc())
            .limit(1)
            .fetchOne()
    }

    @Transactional
    fun updateLessThanCurrentTime() {
        queryFactory
            .update(board)
            .set(board.status, MuckPotStatus.DONE)
            .where(board.meetingTime.lt(LocalDateTime.now()), board.status.eq(MuckPotStatus.IN_PROGRESS))
            .execute()
    }

    private fun lessThanLastId(lastId: Long?): BooleanExpression? {
        return lastId?.let {
            board.id.lt(it)
        }
    }
}
