package com.yapp.muckpot.domains.user.exception

import com.yapp.muckpot.common.BaseErrorCode
import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.enums.StatusCode

enum class UserErrorCode(
    private val status: Int,
    private val reason: String
) : BaseErrorCode {
    USER_NOT_FOUND(StatusCode.BAD_REQUEST.code, "유저를 찾을 수 없습니다."),
    LOGIN_FAIL(StatusCode.UNAUTHORIZED.code, "아이디 혹은 비밀번호가 일치하지 않습니다.");

    override fun toResponseDto(): ResponseDto {
        return ResponseDto(this.status, this.reason, null)
    }
}
