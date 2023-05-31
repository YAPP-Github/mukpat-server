package com.yapp.muckpot.user

import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.ResponseEntityUtil
import com.yapp.muckpot.user.dto.SendEmailAuthRequest
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/v1/emails/request")
    @ApiOperation(value = "이메일 인증 요청")
    fun sendEmailAuth(
        @RequestBody @Valid
        request: SendEmailAuthRequest
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.noContent(userService.sendEmailAuth(request))
    }
}
