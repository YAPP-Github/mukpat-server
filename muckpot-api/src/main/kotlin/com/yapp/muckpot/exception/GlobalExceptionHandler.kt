package com.yapp.muckpot.exception

import com.yapp.muckpot.common.ResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.valueOf
import org.springframework.http.ResponseEntity
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
}
