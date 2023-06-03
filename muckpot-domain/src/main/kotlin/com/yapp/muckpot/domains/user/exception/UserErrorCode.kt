package com.yapp.muckpot.domains.user.exception

import com.yapp.muckpot.common.BaseErrorCode
import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.enums.StatusCode

enum class UserErrorCode(
    private val status: Int,
    private val reason: String
) : BaseErrorCode {
    USER_NOT_FOUND(StatusCode.BAD_REQUEST.code, "유저를 찾을 수 없습니다."),
    LOGIN_FAIL(StatusCode.UNAUTHORIZED.code, "아이디 혹은 비밀번호가 일치하지 않습니다."),
    NO_VERIFY_CODE(StatusCode.UNAUTHORIZED.code, "인증 요청을 먼저 해주세요."),
    EMAIL_VERIFY_FAIL(StatusCode.UNAUTHORIZED.code, "인증에 실패하였습니다.");

    override fun toResponseDto(): ResponseDto {
        return ResponseDto(this.status, this.reason, null)
    }
}
