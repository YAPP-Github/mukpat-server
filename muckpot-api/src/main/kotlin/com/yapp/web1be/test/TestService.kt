package com.yapp.web1be.test

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
