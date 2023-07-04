package com.yapp.muckpot.domains.test.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.domains.test.entity.QTestEntity.testEntity
import com.yapp.muckpot.domains.test.entity.TestEntity
import org.springframework.stereotype.Repository

@Repository
class TestQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {

    fun getTestByName(name: String): TestEntity? {
        return queryFactory.select(testEntity)
            .from(testEntity)
            .where(testEntity.name.eq(name))
            .limit(1)
            .fetchOne()
    }
}
