package com.yapp.muckpot.test

import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.constants.TEST_SAMPLE
import com.yapp.muckpot.common.utils.ResponseEntityUtil
import com.yapp.muckpot.redis.RedisService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@Api(tags = ["테스트 api"], description = "스웨거 사용 기본 템플릿")
@RequestMapping("/api")
class TestController(
    private val testService: TestService,
    private val redisService: RedisService
) {
    private val log = KLogging().logger

    @GetMapping("/v1/test")
    @ApiOperation(value = "Get 테스트")
    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                examples = Example(
                    ExampleProperty(
                        value = TEST_SAMPLE,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    fun getTest(): ResponseEntity<ResponseDto> {
        log.info { "info" }
        log.warn { "warn" }
        log.error { "error" }
        return ResponseEntityUtil.ok(testService.test())
    }

    @PostMapping("/v1/test")
    @ApiOperation(value = "Post 테스트")
    fun postTest(
        @RequestBody @Valid
        request: TestRequest
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.created(testService.save(request))
    }

    @PostMapping("/v1/test/redis")
    @ApiOperation(value = "redis 테스트")
    fun redisTest(): String {
        return redisService.redisString()
    }
}
