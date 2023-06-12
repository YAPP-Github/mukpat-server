package com.yapp.muckpot.common.redisson

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@SpringBootTest
class DistributedLockAopTest @Autowired constructor(
    private val redissonService: RedissonService
) : StringSpec({

    "분산락 적용 동시성 테스트" {
        val successCount = AtomicLong()
        ConcurrencyHelper.execute(
            { redissonService.test(1) },
            successCount
        )
        redissonService.apply shouldBe successCount.toInt()
    }
})

@Service
class RedissonService(
    var apply: Int = 0
) {
    // @DistributedLock("id", "testLock")
    fun test(id: Int) {
        apply += 1
    }
}
