package com.yapp.muckpot.domains.user.entity

import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MuckPotUserTest : FunSpec({
    context("MuckPotUser 유효성 검사") {
        test("올바르지 않은 이메일 포맷") {
            // when & then
            shouldThrow<IllegalArgumentException> {
                MuckPotUser(
                    null,
                    "email#email.com",
                    "pw",
                    "nickname",
                    Gender.MEN,
                    2000,
                    JobGroupMain.DEVELOPMENT,
                    "sub"
                )
            }.message shouldBe "유효한 이메일 형식이 아닙니다"
        }

        test("올바르지 않은 출생년도") {
            // when & then
            shouldThrow<IllegalArgumentException> {
                MuckPotUser(
                    null,
                    "email@email.com",
                    "pw",
                    "nickname",
                    Gender.MEN,
                    1899,
                    JobGroupMain.DEVELOPMENT,
                    "sub"
                )
            }.message shouldBe "잘못된 출생 연도입니다"
        }
    }
})
