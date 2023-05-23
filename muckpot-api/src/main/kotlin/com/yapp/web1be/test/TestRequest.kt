package com.yapp.web1be.test

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

@ApiModel(value = "테스트 Request")
data class TestRequest(
    @field:ApiModelProperty(notes = "이름", required = true, example = "홍길동")
    @field:NotBlank(message = "이름을 입력해 주세요.")
    var name: String?,

    @field:ApiModelProperty(notes = "나이", example = "10")
    var age: Int = 0
)
