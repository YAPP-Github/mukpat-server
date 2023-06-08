package com.yapp.muckpot.domains.board.controller.dto

import com.yapp.muckpot.common.TimeUtil
import com.yapp.muckpot.domains.board.dto.ParticipantReadResponse
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.user.enums.MuckPotStatus

data class MuckpotReadResponse(
    val boardId: Long?,
    val title: String,
    val status: String,
    val todayOrTomorrow: String?,
    val elapsedTime: String,
    val meetingTime: String,
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
            // TODO 만료 된 먹팟 종료 처리 후, 해당 로직은 제거.
            var status = board.status.krNm
            if (board.expired()) {
                status = MuckPotStatus.DONE.krNm
            }
            return MuckpotReadResponse(
                boardId = board.id,
                title = board.title,
                status = status,
                todayOrTomorrow = TimeUtil.isTodayOrTomorrow(board.createdAt.toLocalDate()),
                elapsedTime = TimeUtil.formatElapsedTime(board.createdAt),
                meetingTime = TimeUtil.formatMeetingTime(board.meetingTime),
                meetingPlace = board.location.locationName,
                maxApply = board.maxApply,
                currentApply = board.currentApply,
                participants = participants
            )
        }
    }
}
