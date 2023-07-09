package com.yapp.muckpot.domains.user.controller

import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.constants.EMAIL_AUTH_REQ_RESPONSE
import com.yapp.muckpot.common.constants.LOGIN_RESPONSE
import com.yapp.muckpot.common.constants.NO_BODY_RESPONSE
import com.yapp.muckpot.common.constants.SIGN_UP_RESPONSE
import com.yapp.muckpot.common.utils.ResponseEntityUtil
import com.yapp.muckpot.domains.user.controller.dto.deprecated.LoginRequestV1
import com.yapp.muckpot.domains.user.controller.dto.deprecated.SendEmailAuthRequestV1
import com.yapp.muckpot.domains.user.controller.dto.deprecated.SignUpRequestV1
import com.yapp.muckpot.domains.user.controller.dto.deprecated.VerifyEmailAuthRequestV1
import com.yapp.muckpot.domains.user.service.UserDeprecatedService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Deprecated("V2 배포 후 제거")
@RestController
@Api(tags = ["유저 api - Deprecated"], description = "유저 API - Deprecated")
@RequestMapping("/api")
class UserDeprecatedController(
    private val userDeprecatedService: UserDeprecatedService
) {
    @ApiResponses(
        value = [
            ApiResponse(
                code = 201,
                examples = Example(
                    ExampleProperty(
                        value = SIGN_UP_RESPONSE,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "회원가입 - v1")
    @PostMapping("/v1/users")
    fun signUpV1(
        @RequestBody @Valid
        request: SignUpRequestV1
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.created(userDeprecatedService.signUpV1(request))
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 201,
                examples = Example(
                    ExampleProperty(
                        value = EMAIL_AUTH_REQ_RESPONSE,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "이메일 인증 요청 - v1")
    @PostMapping("/v1/emails/request")
    fun sendEmailAuthV1(
        @RequestBody @Valid
        request: SendEmailAuthRequestV1
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.created(userDeprecatedService.sendEmailAuthV1(request))
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 204,
                examples = Example(
                    ExampleProperty(
                        value = NO_BODY_RESPONSE,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "이메일 인증 검증 - v1")
    @PostMapping("/v1/emails/verify")
    fun verifyEmailAuthV1(
        @RequestBody @Valid
        request: VerifyEmailAuthRequestV1
    ): ResponseEntity<ResponseDto> {
        userDeprecatedService.verifyEmailAuthV1(request)
        return ResponseEntityUtil.noContent()
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                examples = Example(
                    ExampleProperty(
                        value = LOGIN_RESPONSE,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "로그인 - v1")
    @PostMapping("/v1/users/login")
    fun loginV1(
        @RequestBody @Valid
        request: LoginRequestV1
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.ok(userDeprecatedService.loginV1(request))
    }
}
