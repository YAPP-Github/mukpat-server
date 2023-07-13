package com.yapp.muckpot.domains.board.controller

import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.constants.MUCKPOT_SAVE_RESPONSE
import com.yapp.muckpot.common.utils.ResponseEntityUtil
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateResponse
import com.yapp.muckpot.domains.board.controller.dto.deprecated.MuckpotCreateRequestV1
import com.yapp.muckpot.domains.board.controller.dto.deprecated.MuckpotUpdateRequestV1
import com.yapp.muckpot.domains.board.service.BoardDeprecatedService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Deprecated("V2 배포 후 제거")
@RestController
@Api(tags = ["보드 api - Deprecated"], description = "보드 API - Deprecated")
@RequestMapping("/api")
class BoardDeprecatedController(
    private val boardService: BoardDeprecatedService
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
    @ApiOperation(value = "먹팟 글 생성 - v1")
    @PostMapping("/v1/boards")
    fun saveBoardV1(
        @AuthenticationPrincipal userId: Long,
        @RequestBody @Valid
        request: MuckpotCreateRequestV1
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.created(
            MuckpotCreateResponse(
                boardService.saveBoardV1(userId, request)
            )
        )
    }

    @ApiOperation(value = "먹팟 글 수정 - v1")
    @PatchMapping("/v1/boards/{boardId}")
    fun updateBoardV1(
        @AuthenticationPrincipal userId: Long,
        @PathVariable boardId: Long,
        @RequestBody @Valid
        request: MuckpotUpdateRequestV1
    ): ResponseEntity<ResponseDto> {
        boardService.updateBoardAndSendEmailV1(userId, boardId, request)
        return ResponseEntityUtil.noContent()
    }
}
