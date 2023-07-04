package com.yapp.muckpot.test

import com.fasterxml.jackson.annotation.JsonFormat
import com.yapp.muckpot.domains.test.entity.TestEntity
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

@ApiModel(value = "테스트 Response")
data class TestResponse(
    @field:ApiModelProperty(notes = "테스트Dto 아이디", example = "test")
    var id: Long? = null,
    @field:ApiModelProperty(notes = "테스트Dto 이름", example = "test")
    var name: String = "test",
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    @field:ApiModelProperty(notes = "현재 날짜", example = "20230515")
    var currentTime: LocalDate
) {
    companion object {
        fun of(testEntity: TestEntity): TestResponse {
            return TestResponse(testEntity.id, testEntity.name, testEntity.createdAt.toLocalDate())
        }
    }
}
