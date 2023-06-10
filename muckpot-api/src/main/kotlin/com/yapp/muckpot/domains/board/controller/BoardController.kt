package com.yapp.muckpot.domains.board.controller

import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.ResponseEntityUtil
import com.yapp.muckpot.common.dto.CursorPaginationRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateResponse
import com.yapp.muckpot.domains.board.service.BoardService
import com.yapp.muckpot.swagger.MUCKPOT_FIND_ALL
import com.yapp.muckpot.swagger.MUCKPOT_SAVE_RESPONSE
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@Api(tags = ["먹팟 api"], description = "먹팟 API")
@RequestMapping("/api")
class BoardController(
    private val boardService: BoardService
) {
    @ApiResponses(
        value = [
            ApiResponse(
                code = 201,
                examples = Example(
                    ExampleProperty(
                        value = MUCKPOT_SAVE_RESPONSE,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "먹팟 글 생성")
    @PostMapping("/v1/boards")
    fun saveBoard(
        @AuthenticationPrincipal userId: Long,
        @RequestBody @Valid
        request: MuckpotCreateRequest
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.created(
            MuckpotCreateResponse(
                boardService.saveBoard(userId, request)
            )
        )
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                examples = Example(
                    ExampleProperty(
                        value = MUCKPOT_FIND_ALL,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "먹팟 글 리스트 조회")
    @GetMapping("/v1/boards")
    fun findAll(@ModelAttribute request: CursorPaginationRequest): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.ok(boardService.findAllMuckpot(request))
    }
}
