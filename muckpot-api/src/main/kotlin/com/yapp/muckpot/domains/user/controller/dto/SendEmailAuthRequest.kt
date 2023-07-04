package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.common.constants.ONLY_NAVER
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Pattern

@ApiModel(value = "이메일 인증 요청")
data class SendEmailAuthRequest(
    // TODO: 개발 서버에서 메일 정상 작동 확인 후 삼성전자로 변경 필요!
    @field:ApiModelProperty(notes = "이메일", required = true, example = "co@naver.com")
    @field:Pattern(regexp = ONLY_NAVER, message = "현재 버전은 네이버 사우만 이용 가능합니다.") // for test
    val email: String
)
