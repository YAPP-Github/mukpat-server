package com.yapp.muckpot.common.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiParam

@ApiModel(value = "커서기반 페이지요청(무한 스크롤)")
data class CursorPaginationRequest(
    @field:ApiParam(value = "마지막 ID, 기본 null", required = false)
    var lastId: Long? = null,
    @field:ApiParam(value = "스크롤 당 데이터 크기, 기본 $COUNT_PER_SCROLL_DEFAULT", required = false)
    var countPerScroll: Long = COUNT_PER_SCROLL_DEFAULT
) {
    companion object {
        private const val COUNT_PER_SCROLL_DEFAULT = 10L
    }
}
