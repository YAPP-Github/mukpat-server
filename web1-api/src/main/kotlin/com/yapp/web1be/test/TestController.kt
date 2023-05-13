package com.yapp.web1be.test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(private val testService: TestService) {
    @GetMapping
    fun test(): TestEntity {
        return testService.test()
    }
}