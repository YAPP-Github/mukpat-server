package com.yapp.muckpot.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.yapp.muckpot.common.enums.StatusCode

data class ResponseDto(
    val status: Int,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val result: Any? = null
) {
    companion object {
        fun fail(baseErrorCode: BaseErrorCode): ResponseDto {
            return baseErrorCode.toResponseDto()
        }

        fun success(data: Any? = "성공"): ResponseDto {
            return ResponseDto(status = StatusCode.OK.code, null, result = data)
        }

        fun created(data: Any? = "성공"): ResponseDto {
            return ResponseDto(status = StatusCode.CREATED.code, null, result = data)
        }

        fun noContent(): ResponseDto {
            return ResponseDto(status = StatusCode.NO_CONTENT.code, message = null, result = null)
        }
    }
}
