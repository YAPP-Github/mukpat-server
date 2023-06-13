package com.yapp.muckpot.domains.board.controller.dto

import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.exception.MuckPotException
import com.yapp.muckpot.fixture.Fixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalTime

class MuckpotUpdateRequestTest : StringSpec({
    lateinit var request: MuckpotUpdateRequest

    beforeEach {
        request = MuckpotUpdateRequest(
            meetingDate = LocalDate.now(),
            meetingTime = LocalTime.of(12, 15),
            maxApply = 5,
            minAge = 20,
            maxAge = 100,
            locationName = "location",
            locationDetail = null,
            x = 0.0,
            y = 0.0,
            title = "title",
            content = null,
            chatLink = "chat_link"
        )
    }

    "현재 먹팟 참여인원 미만으로 변경할 수 없다." {
        shouldThrow<MuckPotException> {
            request.updateBoard(Fixture.createBoard(currentApply = request.maxApply + 1))
        }.errorCode shouldBe BoardErrorCode.MAX_APPLY_UPDATE_FAIL
    }
})
