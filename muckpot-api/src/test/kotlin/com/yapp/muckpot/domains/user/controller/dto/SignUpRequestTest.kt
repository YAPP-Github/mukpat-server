package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.common.enums.Gender
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator

class SignUpRequestTest : StringSpec({
    lateinit var validator: Validator
    lateinit var request: SignUpRequest

    beforeSpec {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    "이메일 형식 유효 검사" {
        request = SignUpRequest(
            email = "email@validation.com",
            password = "abc1234!",
            nickname = "닉네임",
            jobGroupMain = "개발",
            jobGroupSub = null,
            locationName = "삼전 본사",
            x = 0.0,
            y = 0.0,
            gender = Gender.WOMEN,
            yearOfBirth = 1996
        )

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "현재 버전은 삼성전자 사우만 이용 가능합니다."
        }
    }

    "비밀번호 형식 검사" {
        request = SignUpRequest(
            email = "email@samsung.com",
            password = "12",
            nickname = "닉네임",
            jobGroupMain = "개발",
            jobGroupSub = null,
            locationName = "삼전 본사",
            x = 0.0,
            y = 0.0,
            gender = Gender.WOMEN,
            yearOfBirth = 1996
        )

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "비밀번호는 영문, 숫자 포함 8-20자입니다."
        }
    }

    "닉네임 글자수 2-10자 제한" {
        request = SignUpRequest(
            email = "email@samsung.com",
            password = "abc1234!",
            nickname = "팟",
            jobGroupMain = "개발",
            jobGroupSub = null,
            locationName = "삼전 본사",
            x = 0.0,
            y = 0.0,
            gender = Gender.WOMEN,
            yearOfBirth = 1996
        )

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "닉네임은 2자 이상 10자 이하여야 합니다."
        }
    }

    "직군 소분류 최대 10자 제한" {
        request = SignUpRequest(
            email = "email@samsung.com",
            password = "abc1234!",
            nickname = "닉네임",
            jobGroupMain = "개발",
            jobGroupSub = "아아아아아아아아아아아아아아",
            locationName = "삼전 본사",
            x = 0.0,
            y = 0.0,
            gender = Gender.WOMEN,
            yearOfBirth = 1996
        )

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "직군 소분류는 최대 10자입니다."
        }
    }
})
