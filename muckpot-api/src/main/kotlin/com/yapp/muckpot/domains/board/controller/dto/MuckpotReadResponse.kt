package com.yapp.muckpot.domains.board.controller.dto

import com.yapp.muckpot.common.TimeUtil
import com.yapp.muckpot.common.constants.KR_MM_DD_E
import com.yapp.muckpot.common.constants.a_hhmm
import com.yapp.muckpot.domains.board.dto.ParticipantReadResponse
import com.yapp.muckpot.domains.board.entity.Board

data class MuckpotReadResponse(
    val boardId: Long,
    val title: String,
    val status: String,
    val todayOrTomorrow: String?,
    val elapsedTime: String,
    val meetingDateTime: String,
    val meetingPlace: String,
    val maxApply: Int,
    val currentApply: Int,
    var participants: List<ParticipantReadResponse>
) {
    init {
        limitParticipants()
    }

    private fun limitParticipants() {
        if (participants.size > PARTICIPANTS_MAX_CNT) {
            participants = participants.take(PARTICIPANTS_MAX_CNT) +
                ParticipantReadResponse.otherN(participants.size - PARTICIPANTS_MAX_CNT)
        }
    }

    companion object {
        private const val PARTICIPANTS_MAX_CNT = 5

        fun of(board: Board, participants: List<ParticipantReadResponse>): MuckpotReadResponse {
            return MuckpotReadResponse(
                boardId = board.id ?: 0,
                title = board.title,
                status = board.status.korNm,
                todayOrTomorrow = TimeUtil.isTodayOrTomorrow(board.meetingTime.toLocalDate()),
                elapsedTime = TimeUtil.formatElapsedTime(board.createdAt),
                meetingDateTime = TimeUtil.localeKoreanFormatting(board.meetingTime, "$KR_MM_DD_E $a_hhmm"),
                meetingPlace = board.location.locationName,
                maxApply = board.maxApply,
                currentApply = board.currentApply,
                participants = participants
            )
        }
    }
}
