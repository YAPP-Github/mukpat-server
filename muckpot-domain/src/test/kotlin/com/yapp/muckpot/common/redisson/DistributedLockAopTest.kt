package com.yapp.muckpot.common.redisson

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
        println(redissonService.apply.toString() + " " + successCount.get())
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
        println(apply)
    }
}

class RedissonAopTests {

    var orderService = OrderService()

    @Test
    fun `분산락 적용시 동시요청에 올바르게 재고가 감소해야한다`() {
        // given
        // when
        val successCount = AtomicLong()
        ConcurrencyHelper.execute(
            { orderService.decreaseStock(1) },
            successCount
        )
        // then

        val remain = 100 - successCount.toInt()
        assertThat(orderService.stock).isEqualTo(remain)
    }
}

// @Service
class OrderService(
    var stock: Int = 100
) {

    // @RedissonLock("id", "stockLock")
    fun decreaseStock(id: Int) {
        stock -= 1
    }
}
