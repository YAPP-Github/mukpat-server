package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.common.ONLY_NAVER
import com.yapp.muckpot.common.PW_PATTERN
import com.yapp.muckpot.common.enums.YesNo
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Pattern

@ApiModel(value = "로그인 요청")
data class LoginRequest(
    // TODO 상용 시 samsung.com 으로 변경
    @field:ApiModelProperty(notes = "이메일", required = true, example = "user@naver.com")
    @field:Pattern(regexp = ONLY_NAVER, message = "현재 버전은 네이버 사우만 이용 가능합니다.")
    val email: String,
    @field:ApiModelProperty(notes = "비밀번호", required = true, example = "abcd1234")
    @field:Pattern(
        regexp = PW_PATTERN,
        message = "비밀 번호는 영문과 숫자를 포함하여 8 ~ 20자로 입력해 주세요."
    )
    val password: String,
    @field:ApiModelProperty(notes = "로그인 유지하기", required = true, example = "Y")
    val keep: YesNo = YesNo.Y
)
