package com.yapp.muckpot.domains.board.controller

import com.yapp.muckpot.common.ResponseDto
import com.yapp.muckpot.common.constants.MUCKPOT_FIND_ALL
import com.yapp.muckpot.common.constants.MUCKPOT_FIND_BY_ID
import com.yapp.muckpot.common.constants.MUCKPOT_JOIN_RESPONSE
import com.yapp.muckpot.common.constants.MUCKPOT_REGIONS
import com.yapp.muckpot.common.constants.MUCKPOT_SAVE_RESPONSE
import com.yapp.muckpot.common.dto.CursorPaginationRequest
import com.yapp.muckpot.common.utils.ResponseEntityUtil
import com.yapp.muckpot.common.utils.SecurityContextHolderUtil
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateResponse
import com.yapp.muckpot.domains.board.controller.dto.MuckpotUpdateRequest
import com.yapp.muckpot.domains.board.controller.dto.RegionFilterRequest
import com.yapp.muckpot.domains.board.service.BoardService
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    @PostMapping("/v2/boards")
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
        return ResponseEntityUtil.ok(boardService.findAllBoards(request))
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                examples = Example(
                    ExampleProperty(
                        value = MUCKPOT_FIND_BY_ID,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "먹팟 글 상세 조회")
    @GetMapping("/v1/boards/{boardId}")
    fun findByBoardId(@PathVariable boardId: Long, request: RegionFilterRequest): ResponseEntity<ResponseDto> {
        request.validate()
        return ResponseEntityUtil.ok(
            boardService.findBoardDetailAndVisit(
                boardId,
                SecurityContextHolderUtil.getCredentialOrNull(),
                request
            )
        )
    }

    @ApiOperation(value = "먹팟 글 수정")
    @PatchMapping("/v2/boards/{boardId}")
    fun updateBoard(
        @AuthenticationPrincipal userId: Long,
        @PathVariable boardId: Long,
        @RequestBody @Valid
        request: MuckpotUpdateRequest
    ): ResponseEntity<ResponseDto> {
        boardService.updateBoardAndSendEmail(userId, boardId, request)
        return ResponseEntityUtil.noContent()
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 201,
                examples = Example(
                    ExampleProperty(
                        value = MUCKPOT_JOIN_RESPONSE,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "먹팟 참가 신청")
    @PostMapping("/v1/boards/{boardId}/join")
    fun joinBoard(
        @AuthenticationPrincipal userId: Long,
        @PathVariable boardId: Long
    ): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.created(
            boardService.joinBoard(userId, boardId)
        )
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 204,
                message = "먹팟 글 삭제 성공"
            )
        ]
    )
    @ApiOperation(value = "먹팟 글 삭제")
    @DeleteMapping("/v1/boards/{boardId}")
    fun deleteBoard(
        @AuthenticationPrincipal userId: Long,
        @PathVariable boardId: Long
    ): ResponseEntity<ResponseDto> {
        boardService.deleteBoardAndSendEmail(userId, boardId)
        return ResponseEntityUtil.noContent()
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 204,
                message = "먹팟 글 상태변경 성공"
            )
        ]
    )
    @ApiOperation(value = "먹팟 글 상태변경")
    @PatchMapping("/v1/boards/{boardId}/status")
    fun changeStatus(
        @AuthenticationPrincipal userId: Long,
        @PathVariable boardId: Long,
        @RequestParam("status") status: MuckPotStatus
    ): ResponseEntity<ResponseDto> {
        boardService.changeStatus(userId, boardId, status)
        return ResponseEntityUtil.noContent()
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 204,
                message = "먹팟 참가 신청 취소 성공"
            )
        ]
    )
    @ApiOperation(value = "먹팟 참가 신청 취소")
    @DeleteMapping("/v1/boards/{boardId}/join")
    fun deleteParticipant(
        @AuthenticationPrincipal userId: Long,
        @PathVariable boardId: Long
    ): ResponseEntity<ResponseDto> {
        boardService.cancelJoinAndSendEmail(userId, boardId)
        return ResponseEntityUtil.noContent()
    }

    @ApiResponses(
        value = [
            ApiResponse(
                code = 200,
                examples = Example(
                    ExampleProperty(
                        value = MUCKPOT_REGIONS,
                        mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
                ),
                message = "성공"
            )
        ]
    )
    @ApiOperation(value = "필터링 지역 카테고리 조회")
    @GetMapping("/v1/boards/regions")
    fun findAllRegions(): ResponseEntity<ResponseDto> {
        return ResponseEntityUtil.ok(boardService.findAllRegions())
    }
}
