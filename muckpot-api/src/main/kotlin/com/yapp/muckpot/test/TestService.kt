package com.yapp.muckpot.test

import com.yapp.muckpot.domains.test.entity.TestEntity
import com.yapp.muckpot.domains.test.repository.TestQuerydslRepository
import com.yapp.muckpot.domains.test.repository.TestRepository
import org.springframework.stereotype.Service

@Service
class TestService(
    private val testRepository: TestRepository,
    private val querydslRepository: TestQuerydslRepository
) {

    fun test(): TestResponse {
        testRepository.save(TestEntity(1, "querydsl"))
        return TestResponse.of(querydslRepository.getTestByName("querydsl") ?: TestEntity())
    }
}
