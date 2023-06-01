package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.common.enums.YesNo
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Pattern

@ApiModel(value = "로그인 요청")
data class LoginRequest(
    @field:ApiModelProperty(notes = "이메일", required = true, example = "user@samsung.com")
    @field:Pattern(regexp = "^[A-Za-z0-9._%+-]+@samsung\\.com$", message = "현재 버전은 삼성전자 사우만 이용 가능합니다.")
    val email: String,
    @field:ApiModelProperty(notes = "비밀번호", required = true, example = "abcd1234")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$",
        message = "비밀 번호는 영문과 숫자를 포함하여 8 ~ 20자로 입력해 주세요."
    )
    val password: String,
    @field:ApiModelProperty(notes = "로그인 유지하기", required = true, example = "Y")
    val keep: YesNo = YesNo.Y
)
