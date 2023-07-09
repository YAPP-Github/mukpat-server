package com.yapp.muckpot.domains.user.controller.dto.deprecated

import com.yapp.muckpot.common.constants.ONLY_NAVER
import com.yapp.muckpot.common.constants.PASSWORD_PATTERN_INVALID
import com.yapp.muckpot.common.constants.PW_PATTERN
import com.yapp.muckpot.common.enums.YesNo
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Pattern

@Deprecated("V2 배포 후 제거")
@ApiModel(value = "로그인 요청 - v1")
data class LoginRequestV1(
    // TODO 상용 시 samsung.com 으로 변경
    @field:ApiModelProperty(notes = "이메일", required = true, example = "user@naver.com")
    @field:Pattern(regexp = ONLY_NAVER, message = "현재 버전은 네이버 사우만 이용 가능합니다.")
    val email: String,
    @field:ApiModelProperty(notes = "비밀번호", required = true, example = "abcd1234")
    @field:Pattern(
        regexp = PW_PATTERN,
        message = PASSWORD_PATTERN_INVALID
    )
    val password: String,
    @field:ApiModelProperty(notes = "로그인 유지하기", required = true, example = "Y")
    val keep: YesNo = YesNo.Y
)
