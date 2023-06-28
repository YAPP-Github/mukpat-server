package com.yapp.muckpot.domains.board.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.QBoard.board
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

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
            .select(board.id.max())
            .where(board.id.lt(boardId), board.status.eq(MuckPotStatus.IN_PROGRESS))
            .fetchOne()
    }

    fun findNextId(boardId: Long): Long? {
        return queryFactory.from(board)
            .select(board.id.min())
            .where(board.id.gt(boardId), board.status.eq(MuckPotStatus.IN_PROGRESS))
            .fetchOne()
    }

    private fun lessThanLastId(lastId: Long?): BooleanExpression? {
        return lastId?.let {
            board.id.lt(it)
        }
    }
}
