package com.yapp.muckpot.domains.board.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.querydsl.core.annotations.QueryProjection
import com.yapp.muckpot.domains.user.enums.JobGroupMain

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ParticipantReadResponse constructor(
    @JsonIgnore
    val boardId: Long? = null,
    val userId: Long?,
    val nickName: String,
    val jobGroupMain: String? = null,
    var writer: Boolean? = null
) {
    @QueryProjection
    constructor(
        boardId: Long?,
        userId: Long?,
        nickName: String,
        jobGroupMain: JobGroupMain
    ) : this(boardId, userId, nickName, jobGroupMain.korName, null)

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
