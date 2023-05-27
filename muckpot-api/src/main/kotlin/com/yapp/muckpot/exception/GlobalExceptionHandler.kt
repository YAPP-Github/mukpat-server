package com.yapp.muckpot.exception

import com.yapp.muckpot.common.ResponseDto
import org.springframework.http.HttpStatus.valueOf
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MuckPotException::class)
    fun muckpotGlobalExceptionHandler(e: MuckPotException): ResponseEntity<ResponseDto> {
        val responseDto = e.errorCode.toResponseDto()
        return ResponseEntity.status(valueOf(responseDto.status)).body(responseDto)
    }
}
