package com.yapp.muckpot.fixture

import com.yapp.muckpot.common.AGE_MAX
import com.yapp.muckpot.common.AGE_MIN
import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.common.enums.State
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.entity.ParticipantId
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import java.time.LocalDateTime
import java.util.*

object Fixture {
    fun createUser(
        id: Long? = null,
        email: String = UUID.randomUUID().toString().substring(0, 5) + "@naver.com",
        password: String = "abcd1234",
        nickName: String = UUID.randomUUID().toString(),
        gender: Gender = Gender.MEN,
        yearOfBirth: Int = 2000,
        mainCategory: JobGroupMain = JobGroupMain.DEVELOPMENT,
        subCategory: String? = "subCategory",
        location: Location = Location("userLocation", 40.7128, -74.0060),
        imageUrl: String? = "image_url",
        state: State = State.ACTIVE
    ): MuckPotUser {
        return MuckPotUser(
            id,
            email,
            password,
            nickName,
            gender,
            yearOfBirth,
            mainCategory,
            subCategory,
            location,
            imageUrl,
            state
        )
    }

    fun createBoard(
        id: Long? = null,
        user: MuckPotUser = createUser(),
        title: String = "board_title",
        location: Location = Location("boardLocation", 40.7128, -74.0060),
        locationDetail: String? = null,
        meetingTime: LocalDateTime = LocalDateTime.now(),
        content: String? = "content",
        views: Int = 0,
        currentApply: Int = 0,
        maxApply: Int = 2,
        chatLink: String = "chat_link",
        status: MuckPotStatus = MuckPotStatus.IN_PROGRESS,
        minAge: Int = AGE_MIN,
        maxAge: Int = AGE_MAX,
        state: State = State.ACTIVE
    ): Board {
        return Board(
            id,
            user,
            title,
            location,
            locationDetail,
            meetingTime,
            content,
            views,
            currentApply,
            maxApply,
            chatLink,
            status,
            minAge,
            maxAge,
            state
        )
    }

    fun createParticipant(
        participantId: ParticipantId = ParticipantId(createUser(), createBoard()),
        state: State = State.ACTIVE
    ): Participant {
        return Participant(participantId = participantId, state = state)
    }
}
