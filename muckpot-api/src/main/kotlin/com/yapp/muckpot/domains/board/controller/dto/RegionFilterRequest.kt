package com.yapp.muckpot.domains.board.controller.dto
import io.swagger.annotations.ApiParam
import java.lang.IllegalArgumentException

data class RegionFilterRequest(
    @field:ApiParam(value = "시/도 ID", required = false)
    val cityId: Long? = null,
    @field:ApiParam(value = "군/구 ID", required = false)
    val provinceId: Long? = null
) {
    fun validate() {
        if (provinceId != null && cityId == null) {
            throw IllegalArgumentException("시/도 ID를 입력해 주세요.")
        }
    }
}
