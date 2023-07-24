package com.yapp.muckpot.domains.board.controller.dto

import com.yapp.muckpot.common.TimeUtil
import com.yapp.muckpot.common.constants.KR_YYYY_MM_DD
import com.yapp.muckpot.common.constants.YYYYMMDD
import com.yapp.muckpot.common.constants.a_hhmm
import com.yapp.muckpot.domains.board.entity.Board

data class MuckpotUpdateDetailResponse(
    val boardId: Long,
    val title: String,
    val content: String? = null,
    val chatLink: String,
    val meetingDate: String,
    val meetingTime: String,
    val createDate: String,
    val maxApply: Int,
    var minAge: Int? = null,
    var maxAge: Int? = null,
    val locationName: String,
    // TODO FE 작업 완료 후 nullable 제거
    val addressName: String? = null,
    val x: Double,
    val y: Double,
    val locationDetail: String? = null,
    val userAge: Int?
) {
    private fun changeIsNotAgeLimit() {
        this.minAge = null
        this.maxAge = null
    }

    companion object {
        fun of(
            board: Board,
            userAge: Int? = null
        ): MuckpotUpdateDetailResponse {
            val response = MuckpotUpdateDetailResponse(
                boardId = board.id ?: 0,
                title = board.title,
                content = board.content,
                chatLink = board.chatLink,
                meetingDate = TimeUtil.localeKoreanFormatting(board.meetingTime, YYYYMMDD),
                meetingTime = TimeUtil.localeKoreanFormatting(board.meetingTime, a_hhmm),
                createDate = TimeUtil.localeKoreanFormatting(board.createdAt, KR_YYYY_MM_DD),
                maxApply = board.maxApply,
                minAge = board.minAge,
                maxAge = board.maxAge,
                locationName = board.location.locationName,
                addressName = board.location.addressName,
                x = board.getX(),
                y = board.getY(),
                locationDetail = board.locationDetail,
                userAge = userAge
            )
            if (board.isNotAgeLimit()) {
                response.changeIsNotAgeLimit()
            }
            return response
        }
    }
}
