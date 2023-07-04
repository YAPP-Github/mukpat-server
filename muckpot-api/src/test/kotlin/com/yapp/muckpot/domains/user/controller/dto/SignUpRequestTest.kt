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

    fun createSignUpRequest(
        email: String = "email@samsung.com",
        password: String = "abc1234!",
        nickname: String = "닉네임",
        jobGroupMain: String = "개발",
        jobGroupSub: String = "직군 소분류",
        gender: Gender = Gender.WOMEN,
        yearOfBirth: Int = 1996
    ): SignUpRequest {
        return SignUpRequest(
            email = email,
            password = password,
            nickname = nickname,
            jobGroupMain = jobGroupMain,
            jobGroupSub = jobGroupSub,
            gender = gender,
            yearOfBirth = yearOfBirth
        )
    }

    beforeTest {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    "이메일 형식 유효 검사" {
        val request = createSignUpRequest(email = "email@validation.com")

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "현재 버전은 삼성전자 사우만 이용 가능합니다."
        }
    }

    "비밀번호 형식 검사" {
        val request = createSignUpRequest(password = "12")

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe PASSWORD_PATTERN_INVALID
        }
    }

    "비밀번호에 특수문자를 포함할 수 있다." {
        val request = createSignUpRequest(password = "ab@cd!12$34#")

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 0
    }

    "비밀번호에는 영어, 숫자를 최소한 1개 포함해야 한다" {
        val request = createSignUpRequest(password = "abcdefgh!!")

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe PASSWORD_PATTERN_INVALID
        }
    }

    "닉네임 글자수 2-10자 제한" {
        val request = createSignUpRequest(nickname = "팟")

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "2~10자로 입력해 주세요."
        }
    }

    "직군 소분류 최대 10자 제한" {
        val request = createSignUpRequest(jobGroupSub = "아아아아아아아아아아아아아아")

        val violations: MutableSet<ConstraintViolation<SignUpRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "10자 이하로 입력해 주세요."
        }
    }
})
