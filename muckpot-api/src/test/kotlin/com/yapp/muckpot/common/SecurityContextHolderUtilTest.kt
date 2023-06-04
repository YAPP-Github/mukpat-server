package com.yapp.muckpot.common

import com.yapp.muckpot.common.TestUtil.forceLogin
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.exception.MuckPotException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SecurityContextHolderUtilTest : StringSpec({
    "로그인 유저는 ID를 반환한다." {
        // given
        val userId = 1L
        forceLogin(UserResponse(userId, "user"))

        // when
        val actual = SecurityContextHolderUtil.getCurrentUserId()

        // then
        actual shouldBe userId
    }

    "비 로그인 유저는 예외 발생" {
        // given
        forceLogin(null)

        // when & then
        shouldThrow<MuckPotException> {
            SecurityContextHolderUtil.getCurrentUserId()
        }.errorCode shouldBe UserErrorCode.USER_NOT_FOUND
    }
})
