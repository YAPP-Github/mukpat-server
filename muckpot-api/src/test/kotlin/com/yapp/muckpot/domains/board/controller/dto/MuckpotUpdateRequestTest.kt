package com.yapp.muckpot.domains.board.controller.dto

import Fixture
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.exception.MuckPotException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MuckpotUpdateRequestTest : StringSpec({
    lateinit var request: MuckpotUpdateRequest

    beforeEach {
        request = MuckpotUpdateRequest(
            meetingDate = LocalDate.now().plusDays(2),
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

    "변경된 내용 추출 성공" {
        val meetingTime = LocalDateTime.now().plusDays(1)
        val board = Fixture.createBoard(
            title = "originTitle",
            content = "originContent",
            meetingTime = meetingTime
        )
        val actual = request.createBoardUpdateMailBody(board)

        actual shouldContain "제목이 변경되었습니다."
        actual shouldContain "변경 전 : originTitle"
        actual shouldContain "변경 후 : title"
        actual shouldContain "내용이 변경되었습니다."
        actual shouldContain "변경 전 : originContent"
        actual shouldContain "변경 후 : null"
        actual shouldContain "날짜가 변경되었습니다."
        actual shouldContain "변경 전 : ${meetingTime.toLocalDate()}"
        actual shouldContain "변경 후 : ${request.meetingDate}"
    }
})
