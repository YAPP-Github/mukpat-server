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

    // javax.validation.constraints 오류 (dto validation 오류)
 //   @ExceptionHandler(MethodArgumentNotValidException::class)
//    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ResponseDto> {
        // TODO: validation 여러 개에 어긋날 경우 메시지를 한 번에 다 보내야할지? ex. "message": "삼성만 가능","전화번호 입력 필수"
//      val bindingResult: BindingResult = e.bindingResult
//        val error = e.bindingResult.fieldError?.defaultMessage
//        val errors = bindingResult.allErrors.map { error ->
//            when (error) {
//                is FieldError -> error.defaultMessage.toString()
//                else -> "잘못된 요청입니다."
//            }
//        }
//        val formattedErrors = errors.joinToString("\", \"").removeSurrounding("[", "]")
//        val responseDto = ResponseDto(400, error, null)
//        return ResponseEntity.status(valueOf(responseDto.status)).body(responseDto)
//    }
}
