package com.yapp.muckpot.domains.board.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.domains.board.dto.QRegionDto
import com.yapp.muckpot.domains.board.dto.QRegionDto_CityDto
import com.yapp.muckpot.domains.board.dto.QRegionDto_ProvinceDto
import com.yapp.muckpot.domains.board.dto.RegionDto
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.QBoard.board
import com.yapp.muckpot.domains.board.entity.QCity.city
import com.yapp.muckpot.domains.board.entity.QProvince.province
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
            .select(board.id.min())
            .where(board.id.gt(boardId))
            .fetchOne()
    }

    fun findNextId(boardId: Long): Long? {
        return queryFactory.from(board)
            .select(board.id.max())
            .where(board.id.lt(boardId))
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

    fun findAllRegions(): List<RegionDto> {
        return queryFactory.select(
            QRegionDto(
                board.id,
                QRegionDto_CityDto(
                    province.city.id,
                    province.city.name
                ),
                QRegionDto_ProvinceDto(
                    board.province.id,
                    board.province.name
                )
            )
        )
            .from(board)
            .innerJoin(board.province, province)
            .innerJoin(province.city, city)
            .fetch()
    }

    private fun lessThanLastId(lastId: Long?): BooleanExpression? {
        return lastId?.let {
            board.id.lt(it)
        }
    }
}
