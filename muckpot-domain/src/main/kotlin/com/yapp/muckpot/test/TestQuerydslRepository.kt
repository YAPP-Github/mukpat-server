package com.yapp.muckpot.test

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.test.QTestEntity.testEntity
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
