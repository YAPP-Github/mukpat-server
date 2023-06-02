package com.yapp.muckpot.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service

@Service
class RedisService(private val redisTemplate: RedisTemplate<String, Any>) {

    fun redisString(): String {
        val operations: ValueOperations<String, Any> = redisTemplate.opsForValue()
        operations.set("test", "테스트")
        return operations.get("test") as String
    }

    fun saveRefreshToken(email: String, refreshToken: String) {
        redisTemplate.opsForValue().set(email, refreshToken)
    }
}
