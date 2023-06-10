package com.yapp.muckpot.domains.board.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.QBoard.board
import org.springframework.stereotype.Repository

@Repository
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

    private fun lessThanLastId(lastId: Long?): BooleanExpression? {
        return lastId?.let {
            board.id.lt(it)
        }
    }
}
