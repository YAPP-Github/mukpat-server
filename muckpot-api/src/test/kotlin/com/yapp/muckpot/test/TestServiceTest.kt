package com.yapp.muckpot.test

import com.yapp.muckpot.domains.test.entity.TestEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class TestServiceTest : StringSpec({
    val testService: TestService = mockk()

    "kotest 테스트 " {
        // given
        every { testService.test() } returns TestResponse.of(TestEntity())
        val result = testService.test()

        result.id shouldBe 1
        result.name shouldBe "test"
    }
})
