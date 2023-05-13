package com.yapp.web1be.test

import com.yapp.web1be.test.TestEntity
import org.springframework.stereotype.Service

@Service
class TestService {

    fun test(): TestEntity {
        return TestEntity(1, "test")
    }
}