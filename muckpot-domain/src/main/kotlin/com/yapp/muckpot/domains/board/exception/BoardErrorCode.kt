package com.yapp.muckpot.domains.board.exception

import com.yapp.muckpot.common.BaseErrorCode
import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.enums.StatusCode

enum class BoardErrorCode(
    private val status: Int,
    private val reason: String
) : BaseErrorCode {
    BOARD_NOT_FOUND(StatusCode.BAD_REQUEST.code, "보드를 찾을 수 없습니다.");

    override fun toResponseDto(): ResponseDto {
        return ResponseDto(this.status, this.reason, null)
    }
}
