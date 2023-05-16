package com.yapp.web1be.test

import org.springframework.stereotype.Service

@Service
class TestService {

    fun test(): TestResponse {
        return TestResponse.of(TestEntity())
    }
}