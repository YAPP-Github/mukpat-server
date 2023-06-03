package com.yapp.muckpot.domains.user.controller.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@ApiModel(value = "이메일 인증 검증")
data class VerifyEmailAuthRequest(
    // TODO: 개발 서버에서 메일 정상 작동 확인 후 삼성전자로 변경 필요!
    // @field:Pattern(regexp = "^[A-Za-z0-9._%+-]+@samsung\\.com\$", message = "현재 버전은 삼성전자 사우만 이용 가능합니다.")
    @field:ApiModelProperty(notes = "이메일", required = true, example = "co@samsung.com")
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    @field:Pattern(regexp = "^[A-Za-z0-9._%+-]+@naver\\.com\$", message = "현재 버전은 네이버 사우만 이용 가능합니다.") // for test
    val email: String?,
    @field:ApiModelProperty(notes = "인증 번호", required = true, example = "123456")
    @field:NotBlank(message = "인증 번호를 입력해 주세요.")
    val verificationCode: String?
)
