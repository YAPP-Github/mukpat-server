package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.common.constants.ONLY_NAVER
import com.yapp.muckpot.common.constants.PASSWORD_PATTERN_INVALID
import com.yapp.muckpot.common.constants.PW_PATTERN
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Deprecated("V2 배포 후 제거")
@ApiModel(value = "회원가입 V1")
data class SignUpRequestV1(
    @field:ApiModelProperty(notes = "이메일", required = true, example = "user@naver.com")
    @field:Pattern(regexp = ONLY_NAVER, message = "현재 버전은 네이버 사우만 이용 가능합니다.")
    val email: String,

    @field:ApiModelProperty(notes = "비밀번호", required = true, example = "abc12345")
    @field:Pattern(
        regexp = PW_PATTERN,
        message = PASSWORD_PATTERN_INVALID
    )
    val password: String,

    @field:ApiModelProperty(notes = "닉네임", required = true, example = "맛도리")
    @field:Size(min = 2, max = 10, message = "{min}~{max}자로 입력해 주세요.")
    val nickname: String,

    @field:ApiModelProperty(notes = "직군 대분류", required = true, example = "개발")
    val jobGroupMain: String,

    @field:ApiModelProperty(notes = "직군 소분류", required = false, example = "백엔드")
    @field:Size(max = 10, message = "{max}자 이하로 입력해 주세요.")
    val jobGroupSub: String?,

    @field:ApiModelProperty(notes = "먹팟 위치 이름", required = true, example = "삼성전자 본사")
    val locationName: String,

    @field:ApiModelProperty(notes = "x 좌표", required = true, example = "0.0")
    val x: Double,

    @field:ApiModelProperty(notes = "y 좌표", required = true, example = "0.0")
    val y: Double,

    @field:ApiModelProperty(notes = "성별", required = true, example = "WOMEN")
    val gender: Gender,

    @field:ApiModelProperty(notes = "출생년도", required = true, example = "1987")
    val yearOfBirth: Int
) {
    fun toUser(jobGroupMain: JobGroupMain, encodePw: String): MuckPotUser {
        return MuckPotUser(
            email = email,
            password = encodePw,
            nickName = nickname,
            gender = gender,
            yearOfBirth = yearOfBirth,
            mainCategory = jobGroupMain,
            subCategory = jobGroupSub
        )
    }
}
