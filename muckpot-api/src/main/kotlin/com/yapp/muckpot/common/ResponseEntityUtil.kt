package com.yapp.muckpot.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ResponseEntityUtil {
    fun ok(body: Any?): ResponseEntity<ResponseDto> {
        return ResponseEntity.ok(ResponseDto.success(body))
    }

    fun created(body: Any?): ResponseEntity<ResponseDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.created(body))
    }

    fun noContent(body: Any?): ResponseEntity<ResponseDto> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseDto.noContent())
    }
}
