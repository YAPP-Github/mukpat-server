package com.yapp.muckpot.common.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel(value = "커서기반 페이지요청 결과(무한 스크롤)")
data class CursorPaginationResponse<T>(
    @field:ApiModelProperty(notes = "데이터 리스트")
    val list: List<T>,
    @field:ApiModelProperty(example = "11", notes = "마지막 id")
    val lastId: Long? = null
)
