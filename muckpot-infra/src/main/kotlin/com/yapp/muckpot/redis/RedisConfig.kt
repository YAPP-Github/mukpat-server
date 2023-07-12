package com.yapp.muckpot.redis

import com.yapp.muckpot.redis.constants.REGIONS_CACHE_NAME
import com.yapp.muckpot.redis.dto.RegionResponse
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    @Value("\${spring.redis.host}")
    private val redisHost: String,

    @Value("\${spring.redis.port}")
    private val redisPort: Int
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(redisHost, redisPort)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setConnectionFactory(redisConnectionFactory())
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        return redisTemplate
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer().address = "$REDISSON_HOST_PREFIX$redisHost:$redisPort"
        return Redisson.create(config)
    }

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        val cacheDefaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheDefaultConfig).also {
                regionsCacheConfig(it)
            }.build()
    }

    private fun regionsCacheConfig(cacheBuilder: RedisCacheManagerBuilder) {
        cacheBuilder
            .withCacheConfiguration(
                REGIONS_CACHE_NAME,
                RedisCacheConfiguration.defaultCacheConfig()
                    .serializeValuesWith(
                        SerializationPair.fromSerializer(Jackson2JsonRedisSerializer(RegionResponse::class.java))
                    )
            )
    }

    companion object {
        private const val REDISSON_HOST_PREFIX = "redis://"
    }
}
