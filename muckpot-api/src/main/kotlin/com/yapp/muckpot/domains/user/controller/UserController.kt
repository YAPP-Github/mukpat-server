package com.yapp.muckpot.domains.user.controller

import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.ResponseEntityUtil
import com.yapp.muckpot.domains.user.controller.dto.LoginRequest
import com.yapp.muckpot.domains.user.service.UserService
import com.yapp.muckpot.swagger.LOGIN_RESPONSE
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

@RestController
@Api(tags = ["유저 api"], description = "유저 API")
@RequestMapping("/api")
class UserController(
    private val userService: UserService
) {

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
    @ApiOperation(value = "로그인")
    @PostMapping("/v1/users/login")
    fun login(
        @RequestBody @Valid
        request: LoginRequest
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.ok(userService.login(request))
    }
}
