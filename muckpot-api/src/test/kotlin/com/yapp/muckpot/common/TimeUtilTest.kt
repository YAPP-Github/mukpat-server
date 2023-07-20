package com.yapp.muckpot.common

import com.yapp.muckpot.common.constants.A_DAY_AGO
import com.yapp.muckpot.common.constants.KR_MM_DD_E
import com.yapp.muckpot.common.constants.KR_YYYY_MM_DD
import com.yapp.muckpot.common.constants.KR_YYYY_MM_DD_E
import com.yapp.muckpot.common.constants.N_HOURS_AGO
import com.yapp.muckpot.common.constants.N_MINUTES_AGO
import com.yapp.muckpot.common.constants.TODAY_KR
import com.yapp.muckpot.common.constants.TOMORROW_KR
import com.yapp.muckpot.common.constants.a_hhmm
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDateTime

class TimeUtilTest : FunSpec({
    val today = LocalDateTime.now()
    val yesterday = today.minusDays(1)
    val tomorrow = today.plusDays(1)

    context("오늘 내일 테스트") {
        withData(
            nameFn = { "${it.b} 응답하는 경우" },
            row(today, TODAY_KR),
            row(tomorrow, TOMORROW_KR),
            row(yesterday, null)
        ) { (localDate, expect) ->
            // when & then
            TimeUtil.isTodayOrTomorrow(localDate.toLocalDate()) shouldBe expect
        }
    }

    context("시간 정책 - 60분 미만") {
        withData(
            nameFn = { "${it.a}분 전" },
            row(0L),
            row(1L),
            row(59L)
        ) { (minute) ->
            // when & then
            TimeUtil.formatElapsedTime(
                today.minusMinutes(minute)
            ) shouldBe N_MINUTES_AGO.format(minute)
        }
    }

    context("시간 정책 - 1시간 이상 ~ 24시간 미만") {
        withData(
            nameFn = { "${it.a}시간 전" },
            row(1L),
            row(23L)
        ) { (hour) ->
            // when & then
            TimeUtil.formatElapsedTime(
                today.minusHours(hour)
            ) shouldBe N_HOURS_AGO.format(hour)
        }
    }

    context("시간 정책 - 24시간 이상 ~ 48시간 미만") {
        withData(
            nameFn = { "${it.a}시간 이전" },
            row(24L),
            row(47L)
        ) { (hour) ->
            // when & then
            TimeUtil.formatElapsedTime(
                today.minusHours(hour)
            ) shouldBe A_DAY_AGO
        }
    }

    context("시간 정책 - 48시간 이상") {
        withData(
            nameFn = { "${it.a}시간 이전" },
            row(48L),
            row(100L)
        ) { (hour) ->
            val localDateTime = today.minusHours(hour)
            // when & then
            TimeUtil.formatElapsedTime(localDateTime) shouldBe localDateTime.toLocalDate().toString()
        }
    }

    context("a_hhmm 포 테스트") {
        withData(
            nameFn = { "${it.a}:${it.b}" },
            row(0, 0, "오전"),
            row(11, 59, "오전"),
            row(12, 0, "오후"),
            row(23, 59, "오후")
        ) { (hour, minute, amPm) ->
            // given
            val localDateTime = LocalDateTime.of(2023, 1, 1, hour, minute)
            // when & then
            TimeUtil.localeKoreanFormatting(localDateTime, a_hhmm) shouldContain amPm
        }
    }

    test("KR_MM_DD_E 포맷 테스트") {
        // given
        val localDateTime = LocalDateTime.of(2020, 1, 2, 12, 0)
        // when & then
        TimeUtil.localeKoreanFormatting(localDateTime, KR_MM_DD_E) shouldBe "01월 02일 (목)"
    }

    test("KR_YYYY_MM_DD_E 포맷 테스트") {
        // given
        val localDateTime = LocalDateTime.of(2020, 1, 2, 12, 0)
        // when & then
        TimeUtil.localeKoreanFormatting(localDateTime, KR_YYYY_MM_DD_E) shouldBe "2020년 01월 02일 (목)"
    }

    test("KR_YYYY_MM_DD 포맷 테스트") {
        // given
        val localDateTime = LocalDateTime.of(2020, 1, 2, 12, 0)
        // when & then
        TimeUtil.localeKoreanFormatting(localDateTime, KR_YYYY_MM_DD) shouldBe "2020년 01월 02일"
    }
})
