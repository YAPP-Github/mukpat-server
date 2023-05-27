package com.yapp.muckpot.domains.user.entity

import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.enums.Gender
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point

class MuckPotUserTest : FunSpec({
    val point: Point = GeometryFactory().createPoint(Coordinate(40.7128, -74.0060))

    context("MuckPotUser 유효성 검사") {
        test("올바르지 않은 이메일 포맷") {
            // when & then
            shouldThrow<IllegalArgumentException> {
                MuckPotUser(
                    null, "email#email.com", "pw", "nickname",
                    Gender.MEN, 2000, "main", "sub", Location("location", point), "url"
                )
            }.message shouldBe "유효한 이메일 형식이 아닙니다"
        }

        test("올바르지 않은 출생년도") {
            // when & then
            shouldThrow<IllegalArgumentException> {
                MuckPotUser(
                    null, "email@email.com", "pw", "nickname",
                    Gender.MEN, 1899, "main", "sub", Location("location", point), "url"
                )
            }.message shouldBe "잘못된 출생 연도입니다"
        }
    }
})
