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
    EMAIL_VERIFY_FAIL(StatusCode.UNAUTHORIZED.code, "인증에 실패하였습니다."),
    ALREADY_EXISTS_USER(StatusCode.UNAUTHORIZED.code, "이미 가입한 유저입니다."),
    WRONG_MAIN_JOB(StatusCode.BAD_REQUEST.code, "잘못된 직군 대분류입니다."),
    NOT_FOUND_TOKEN(StatusCode.BAD_REQUEST.code, "토큰 정보를 찾을 수 없습니다."),
    IS_BLACKLIST_TOKEN(StatusCode.BAD_REQUEST.code, "로그아웃 된 토큰 정보입니다."),
    FAIL_JWT_REISSUE(StatusCode.BAD_REQUEST.code, "JWT 재발급 실패");

    override fun toResponseDto(): ResponseDto {
        return ResponseDto(this.status, this.reason, null)
    }
}
