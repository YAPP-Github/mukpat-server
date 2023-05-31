package com.yapp.muckpot.user.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@ApiModel(value = "이메일 인증 요청 Request")
data class SendEmailAuthRequest(
    // @field:Pattern(regexp = "^[A-Za-z0-9._%+-]+@samsung\\.com\$", message = "현재 버전은 삼성전자 사우만 이용 가능합니다.")
    @field:ApiModelProperty(notes = "이메일", required = true, example = "co@samsung.com")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    @field:Pattern(regexp = "^[A-Za-z0-9._%+-]+@naver\\.com\$", message = "현재 버전은 삼성전자 사우만 이용 가능합니다.")
    var email: String?
)
