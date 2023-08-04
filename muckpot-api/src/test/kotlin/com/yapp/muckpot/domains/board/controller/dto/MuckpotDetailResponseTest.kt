package com.yapp.muckpot.domains.board.controller.dto

import com.yapp.muckpot.common.constants.AGE_MAX
import com.yapp.muckpot.common.constants.AGE_MIN
import com.yapp.muckpot.domains.board.dto.ParticipantReadResponse
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class MuckpotDetailResponseTest : StringSpec({
    "로그인 유저가 참여했다면 가장 첫번째로 정렬된다." {
        // given
        val loginUser = UserResponse(2, "user2")
        val participants = listOf(
            ParticipantReadResponse(1, 1, "user1", JobGroupMain.DEVELOPMENT),
            ParticipantReadResponse(1, loginUser.userId, loginUser.nickName, JobGroupMain.DEVELOPMENT),
            ParticipantReadResponse(1, 3, "user3", JobGroupMain.DEVELOPMENT)
        )
        val response = MuckpotDetailResponse.of(Fixture.createBoard(), participants)
        // when
        response.sortParticipantsByLoginUser(loginUser)
        // then
        response.participants[0].userId shouldBe loginUser.userId
        response.participants[0].nickName shouldBe loginUser.nickName
    }

    "가장 첫번째 참여한 인원이 조직장이 된다." {
        // given
        val participants = listOf(
            ParticipantReadResponse(1, 1, "user1", JobGroupMain.DEVELOPMENT),
            ParticipantReadResponse(1, 2, "user2", JobGroupMain.DEVELOPMENT),
            ParticipantReadResponse(1, 3, "user3", JobGroupMain.DEVELOPMENT)
        )
        // when
        val response = MuckpotDetailResponse.of(Fixture.createBoard(), participants)
        // then
        response.participants[0].writer shouldBe true
    }

    "20 ~ 100은, 나이제한이 없는 경우" {
        // given
        val participants = listOf(
            ParticipantReadResponse(1, 1, "user1", JobGroupMain.DEVELOPMENT)
        )
        // when
        val response = MuckpotDetailResponse.of(
            Fixture.createBoard(
                minAge = AGE_MIN,
                maxAge = AGE_MAX
            ),
            participants
        )
        // then
        response.minAge shouldBe null
        response.maxAge shouldBe null
    }

    "현재시간 미만은 isOutOfDate=true" {
        // given
        val participants = listOf(
            ParticipantReadResponse(1, 1, "user1", JobGroupMain.DEVELOPMENT)
        )
        // when
        val response = MuckpotDetailResponse.of(
            Fixture.createBoard(
                meetingTime = LocalDateTime.now().minusMinutes(10)
            ),
            participants
        )
        // then
        response.isOutOfDate shouldBe true
    }

    "현재시간 이후는 isOutOfDate=false" {
        // given
        val participants = listOf(
            ParticipantReadResponse(1, 1, "user1", JobGroupMain.DEVELOPMENT)
        )
        // when
        val response = MuckpotDetailResponse.of(
            Fixture.createBoard(
                meetingTime = LocalDateTime.now().plusMinutes(10)
            ),
            participants
        )
        // then
        response.isOutOfDate shouldBe false
    }
})
