package com.yapp.muckpot.common.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.muckpot.common.ResponseDto
import org.springframework.http.MediaType
import javax.servlet.http.HttpServletResponse

/**
 * filter 예외 발생 시 응답에 직접 데이터를 넣어주기 위한 클래스
 */
object ResponseWriter {
    private val objectMapper = ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)

    /**
     * Response에 직접 예외 응답 작성
     */
    fun writeResponse(response: HttpServletResponse, statusCode: Int, message: String) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = statusCode
        response.outputStream.use { os ->
            objectMapper.writeValue(
                os,
                ResponseDto(
                    status = statusCode,
                    message
                )
            )
            os.flush()
        }
    }
}
