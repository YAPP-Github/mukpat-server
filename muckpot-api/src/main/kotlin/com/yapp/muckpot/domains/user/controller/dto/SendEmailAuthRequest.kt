package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.common.constants.ONLY_SAMSUNG
import com.yapp.muckpot.common.constants.ONLY_SAMSUNG_EXP_MSG
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Pattern

@ApiModel(value = "이메일 인증 요청")
data class SendEmailAuthRequest(
    @field:ApiModelProperty(notes = "이메일", required = true, example = "user@samsung.com")
    @field:Pattern(regexp = ONLY_SAMSUNG, message = ONLY_SAMSUNG_EXP_MSG)
    val email: String
)
