package com.yapp.muckpot.domains.board.service

import com.ninjasquad.springmockk.MockkBean
import com.yapp.muckpot.common.dto.CursorPaginationRequest
import com.yapp.muckpot.domains.board.dto.ParticipantReadResponse
import com.yapp.muckpot.domains.board.repository.BoardQuerydslRepository
import com.yapp.muckpot.domains.board.repository.ParticipantQuerydslRepository
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.fixture.Fixture
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BoardServiceMockTest @Autowired constructor(
    private val boardService: BoardService,
    @MockkBean
    private val boardQuerydslRepository: BoardQuerydslRepository,
    @MockkBean
    private val participantQuerydslRepository: ParticipantQuerydslRepository
) : StringSpec({
    val allBoardSize = 3

    val allBoard = listOf(
        Fixture.createBoard(id = 1, title = "board1"),
        Fixture.createBoard(id = 2, title = "board2"),
        Fixture.createBoard(id = 3, title = "board3")
    )
    val participantResponses = listOf(
        ParticipantReadResponse(boardId = 1, userId = 1, nickName = "user1"),
        ParticipantReadResponse(boardId = 1, userId = 2, nickName = "user2"),
        ParticipantReadResponse(boardId = 1, userId = 3, nickName = "user3"),
        ParticipantReadResponse(boardId = 1, userId = 4, nickName = "user4"),
        ParticipantReadResponse(boardId = 1, userId = 5, nickName = "user5"),
        ParticipantReadResponse(boardId = 1, userId = 6, nickName = "user6"),
        ParticipantReadResponse(boardId = 1, userId = 7, nickName = "user7"),
        ParticipantReadResponse(boardId = 1, userId = 8, nickName = "user8"),
        ParticipantReadResponse(boardId = 2, userId = 1, nickName = "user1"),
        ParticipantReadResponse(boardId = 2, userId = 2, nickName = "user2"),
        ParticipantReadResponse(boardId = 3, userId = 1, nickName = "user1")
    )

    beforeTest {
        // given
        every { boardQuerydslRepository.findAllWithPagination(any(), any()) } returns allBoard
        every { participantQuerydslRepository.findByBoardIds(any()) } returns participantResponses
    }

    "모든 먹팟 조회 성공" {
        // when
        val actual = boardService.findAllMuckpot(CursorPaginationRequest(null, allBoardSize.toLong()))
        // then
        actual.list shouldHaveSize 3
        actual.lastId shouldBe allBoard.last().id
    }

    "참가자가 6명을 넘어가면 마지막에 외N 명으로 응답한다" {
        // when
        val actual = boardService.findAllMuckpot(CursorPaginationRequest(null, allBoardSize.toLong()))
        // then
        actual.list[0].participants.last().nickName shouldBe "외 3명"
    }

    "현재 시간 이전인 경우 모집마감 으로 바꾸어 응답한다." {
        // when
        val actual = boardService.findAllMuckpot(CursorPaginationRequest(null, allBoardSize.toLong()))
        // then
        actual.list[0].status shouldBe MuckPotStatus.DONE.krNm
    }
})
