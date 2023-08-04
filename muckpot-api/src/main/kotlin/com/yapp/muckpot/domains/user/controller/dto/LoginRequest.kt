package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.common.constants.ONLY_SAMSUNG
import com.yapp.muckpot.common.constants.ONLY_SAMSUNG_EXP_MSG
import com.yapp.muckpot.common.constants.PASSWORD_PATTERN_INVALID
import com.yapp.muckpot.common.constants.PW_PATTERN
import com.yapp.muckpot.common.enums.YesNo
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Pattern

@ApiModel(value = "로그인 요청")
data class LoginRequest(
    @field:ApiModelProperty(notes = "이메일", required = true, example = "user@samsung.com")
    @field:Pattern(regexp = ONLY_SAMSUNG, message = ONLY_SAMSUNG_EXP_MSG)
    val email: String,
    @field:ApiModelProperty(notes = "비밀번호", required = true, example = "abc12345")
    @field:Pattern(
        regexp = PW_PATTERN,
        message = PASSWORD_PATTERN_INVALID
    )
    val password: String,
    @field:ApiModelProperty(notes = "로그인 유지하기", required = true, example = "Y")
    val keep: YesNo = YesNo.Y
)
