package com.yapp.muckpot.domains.board.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.querydsl.core.annotations.QueryProjection
import com.yapp.muckpot.domains.user.enums.JobGroupMain

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ParticipantReadResponse constructor(
    val boardId: Long? = null,
    val userId: Long?,
    val nickName: String,
    val jonGroupMain: JobGroupMain? = null,
    var isWriter: Boolean? = null
) {
    @QueryProjection
    constructor(
        boardId: Long?,
        userId: Long?,
        nickName: String,
        jonGroupMain: JobGroupMain?
    ) : this(boardId, userId, nickName, jonGroupMain, null)

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
