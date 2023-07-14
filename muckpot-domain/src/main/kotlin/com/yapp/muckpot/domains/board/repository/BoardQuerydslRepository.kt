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

    fun findPrevId(boardId: Long, cityId: Long? = null, provinceId: Long? = null): Long? {
        return queryFactory.from(board)
            .innerJoin(board.province, province)
            .select(board.id.min())
            .where(board.id.gt(boardId), cityIdEqBoard(cityId), provinceIdEqBoard(provinceId))
            .fetchOne()
    }

    fun findNextId(boardId: Long, cityId: Long? = null, provinceId: Long? = null): Long? {
        return queryFactory.from(board)
            .innerJoin(board.province, province)
            .select(board.id.max())
            .where(board.id.lt(boardId), cityIdEqBoard(cityId), provinceIdEqBoard(provinceId))
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
                board.status,
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

    private fun cityIdEqBoard(cityId: Long?): BooleanExpression? {
        return cityId?.let {
            board.province.city.id.eq(cityId)
        }
    }

    private fun provinceIdEqBoard(provinceId: Long?): BooleanExpression? {
        return provinceId?.let {
            board.province.id.eq(provinceId)
        }
    }
}
