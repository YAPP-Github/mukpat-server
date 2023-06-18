package com.yapp.muckpot.domains.board.exception

import com.yapp.muckpot.common.BaseErrorCode
import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.enums.StatusCode

enum class ParticipantErrorCode(
    private val status: Int,
    private val reason: String
) : BaseErrorCode {
    ALREADY_JOIN(StatusCode.BAD_REQUEST.code, "이미 참여한 유저입니다."),
    PARTICIPANT_NOT_FOUND(StatusCode.BAD_REQUEST.code, "참여 정보를 찾을 수 없습니다."),
    WRITER_MUST_JOIN(StatusCode.BAD_REQUEST.code, "글 작성자는 참가 신청 취소 불가합니다.");

    override fun toResponseDto(): ResponseDto {
        return ResponseDto(this.status, this.reason, null)
    }
}
