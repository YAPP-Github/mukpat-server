package com.yapp.muckpot.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.yapp.muckpot.common.ResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.valueOf
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.validation.ValidationException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MuckPotException::class)
    fun muckpotGlobalExceptionHandler(exception: MuckPotException): ResponseEntity<ResponseDto> {
        val responseDto = exception.errorCode.toResponseDto()
        return ResponseEntity.status(valueOf(responseDto.status)).body(responseDto)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun internalServerErrorHandler(exception: Exception): ResponseEntity<ResponseDto> {
        return ResponseEntity.internalServerError()
            .body(ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequestErrorHandler(exception: Exception): ResponseEntity<ResponseDto> {
        return ResponseEntity.badRequest()
            .body(ResponseDto(HttpStatus.BAD_REQUEST.value(), exception.message))
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class, ValidationException::class])
    fun methodArgumentNotValidExceptionHandler(exception: Exception): ResponseEntity<ResponseDto> {
        var message = exception.message
        if (exception is MethodArgumentNotValidException && exception.hasErrors()) {
            message = exception.allErrors.firstOrNull()?.defaultMessage ?: exception.message
        }
        return ResponseEntity.badRequest()
            .body(ResponseDto(HttpStatus.BAD_REQUEST.value(), message))
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun httpMessageNotReadableExceptionHandler(ex: HttpMessageNotReadableException): ResponseEntity<ResponseDto> {
        var message = ex.message
        val cause = ex.cause
        if (cause is MissingKotlinParameterException) {
            val name = cause.parameter.name
            message = "{$name}값이 필요합니다."
        } else if (cause is InvalidFormatException) {
            message = "${cause.value}은(는) 유효하지 않은 포맷입니다."
        }
        return ResponseEntity.badRequest()
            .body(ResponseDto(HttpStatus.BAD_REQUEST.value(), message))
    }
}
