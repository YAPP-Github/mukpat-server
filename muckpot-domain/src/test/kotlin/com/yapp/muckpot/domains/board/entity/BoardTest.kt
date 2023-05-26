package com.yapp.muckpot.domains.board.entity

import com.yapp.muckpot.common.Location
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.LocationType
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

class BoardTest : FunSpec({
    val user: MuckPotUser = mockk()
    val location: Location = mockk()

    context("Board 유효성 검사") {
        test("minAge는 maxAge 보다 작아야 한다.") {
            shouldThrow<IllegalArgumentException> {
                Board(
                    null, user, "title", location, null, LocationType.COMPANY,
                    LocalDateTime.now(), "content", 0, 0, 3, "link",
                    MuckPotStatus.IN_PROGRESS, 25, 21
                )
            }.message shouldBe "최소나이는 최대나이보다 작아야 합니다"
        }
    }
})
