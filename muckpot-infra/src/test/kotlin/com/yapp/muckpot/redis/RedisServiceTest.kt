package com.yapp.muckpot.redis

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@Import(RedisConfig::class, RedisService::class)
class RedisServiceTest constructor(
    private val redisService: RedisService
) : StringSpec({
    "샘플 테스트" {
        val actual = redisService.redisString()

        actual shouldBe "테스트"
    }
})
