package com.yapp.muckpot.domains.board.controller.dto

import com.yapp.muckpot.common.constants.CHAT_LINK_MAX
import com.yapp.muckpot.common.constants.CONTENT_MAX
import com.yapp.muckpot.common.constants.MAX_APPLY_INVALID
import com.yapp.muckpot.common.constants.NOT_BLANK_COMMON
import com.yapp.muckpot.common.constants.TITLE_MAX
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalTime
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator

class MuckpotCreateRequestTest : StringSpec({
    lateinit var validator: Validator

    fun createMuckpotCreateRequest(
        meetingDate: LocalDate = LocalDate.now(),
        meetingTime: LocalTime = LocalTime.of(12, 1),
        maxApply: Int = 10,
        minAge: Int = 20,
        maxAge: Int = 100,
        locationName: String = "location",
        locationDetail: String? = null,
        x: Double = 0.0,
        y: Double = 0.0,
        title: String = "title",
        content: String? = null,
        chatLink: String = "chat_link",
        region_1depth_name: String = "서울특별시",
        region_2depth_name: String = "강남구"
    ): MuckpotCreateRequest {
        return MuckpotCreateRequest(
            meetingDate = meetingDate,
            meetingTime = meetingTime,
            maxApply = maxApply,
            minAge = minAge,
            maxAge = maxAge,
            locationName = locationName,
            locationDetail = locationDetail,
            x = x,
            y = y,
            title = title,
            content = content,
            chatLink = chatLink,
            region_1depth_name = region_1depth_name,
            region_2depth_name = region_2depth_name
        )
    }

    beforeTest {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    "제목은 최대 100자" {
        val request = createMuckpotCreateRequest(title = "X".repeat(TITLE_MAX + 1))

        val violations: MutableSet<ConstraintViolation<MuckpotCreateRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "제목은 $TITLE_MAX(자)를 넘을 수 없습니다."
        }
    }

    "내용은 최대 2000자" {
        val request = createMuckpotCreateRequest(content = "X".repeat(CONTENT_MAX + 1))

        val violations: MutableSet<ConstraintViolation<MuckpotCreateRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "내용은 $CONTENT_MAX(자)를 넘을 수 없습니다."
        }
    }

    "링크는 최대 300자" {
        val request = createMuckpotCreateRequest(chatLink = "X".repeat(CHAT_LINK_MAX + 1))

        val violations: MutableSet<ConstraintViolation<MuckpotCreateRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe "링크는 $CHAT_LINK_MAX(자)를 넘을 수 없습니다."
        }
    }

    "chatLink는 공백이 될 수 없다." {
        val request = createMuckpotCreateRequest(chatLink = "  ")

        val violations: MutableSet<ConstraintViolation<MuckpotCreateRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe NOT_BLANK_COMMON
        }
    }

    "시/도는 공백이 될 수 없다." {
        val request = createMuckpotCreateRequest(region_1depth_name = " ")

        val violations: MutableSet<ConstraintViolation<MuckpotCreateRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe NOT_BLANK_COMMON
        }
    }

    "구/군은 공백이 될 수 없다." {
        val request = createMuckpotCreateRequest(region_2depth_name = " ")

        val violations: MutableSet<ConstraintViolation<MuckpotCreateRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe NOT_BLANK_COMMON
        }
    }

    "참여인원은 100명을 넘을 수 없다." {
        val request = createMuckpotCreateRequest(maxApply = 101)

        val violations: MutableSet<ConstraintViolation<MuckpotCreateRequest>> = validator.validate(request)
        violations.size shouldBe 1
        for (violation in violations) {
            violation.message shouldBe MAX_APPLY_INVALID.format(2, 100)
        }
    }
})
