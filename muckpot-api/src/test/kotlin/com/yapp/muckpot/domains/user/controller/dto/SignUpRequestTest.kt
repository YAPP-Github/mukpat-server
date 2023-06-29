package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.common.constants.PASSWORD_PATTERN_INVALID
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
            violation.message shouldBe "현재 버전은 네이버 사우만 이용 가능합니다."
        }
    }

    "비밀번호 형식 검사" {
        request = SignUpRequest(
            email = "email@naver.com",
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
            violation.message shouldBe PASSWORD_PATTERN_INVALID
        }
    }

    "닉네임 글자수 2-10자 제한" {
        request = SignUpRequest(
            email = "email@naver.com",
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
            violation.message shouldBe "2~10자로 입력해 주세요."
        }
    }

    "직군 소분류 최대 10자 제한" {
        request = SignUpRequest(
            email = "email@naver.com",
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
            violation.message shouldBe "10자 이하로 입력해 주세요."
        }
    }
})
