package com.yapp.muckpot.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Service
import java.time.Duration

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

    fun setDataExpireWithNewest(key: String, value: String, duration: Long) {
        if (redisTemplate.hasKey(key)) { // 만료돼지않은 인증코드 값이 잔존할 경우 삭제 후 재발급
            redisTemplate.delete(key)
        }
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(duration))
    }
    fun deleteData(key: String) {
        redisTemplate.delete(key)
    }

    fun getData(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }
}
