package com.yapp.web1be.test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping
    fun test(): TestEntity {
        return TestEntity(1, "test")
    }
}
