package com.yapp.web1be.test

import com.yapp.web1be.swagger.TEST_SAMPLE
import io.swagger.annotations.*
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
class TestController(private val testService: TestService) {
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
    fun getTest(): TestResponse {
        return testService.test()
    }

    @PostMapping("/v1/test")
    @ApiOperation(value = "Post 테스트")
    fun postTest(@RequestBody @Valid request: TestRequest): ResponseEntity<String> {
        return ResponseEntity.ok("${request.name}(${request.age})")
    }
}
