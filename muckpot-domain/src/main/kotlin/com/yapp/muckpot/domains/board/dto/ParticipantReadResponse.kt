package com.yapp.muckpot.domains.board.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.annotations.QueryProjection

data class ParticipantReadResponse @QueryProjection constructor(
    @JsonIgnore
    val boardId: Long? = null,
    val userId: Long?,
    val nickName: String
) {
    companion object {
        fun otherN(otherCnt: Int): ParticipantReadResponse {
            return ParticipantReadResponse(
                null,
                null,
                "외 ${otherCnt}명"
            )
        }
    }
}
