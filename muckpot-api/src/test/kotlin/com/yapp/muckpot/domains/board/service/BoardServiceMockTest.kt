package com.yapp.muckpot.domains.board.service

import Fixture
import com.ninjasquad.springmockk.MockkBean
import com.yapp.muckpot.common.constants.TODAY_KR
import com.yapp.muckpot.common.constants.TOMORROW_KR
import com.yapp.muckpot.common.dto.CursorPaginationRequest
import com.yapp.muckpot.domains.board.dto.ParticipantReadResponse
import com.yapp.muckpot.domains.board.repository.BoardQuerydslRepository
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.ParticipantQuerydslRepository
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@SpringBootTest
class BoardServiceMockTest @Autowired constructor(
    private val boardService: BoardService,
    @MockkBean
    private val boardQuerydslRepository: BoardQuerydslRepository,
    @MockkBean
    private val participantQuerydslRepository: ParticipantQuerydslRepository,
    @MockkBean
    private val boardRepository: BoardRepository
) : FunSpec({
    context("findAllMuckpot 테스트") {
        val allBoardSize = 3
        val allBoard = listOf(
            Fixture.createBoard(id = 1, title = "board1").apply { meetingTime = LocalDateTime.MIN },
            Fixture.createBoard(id = 2, title = "board2").apply { meetingTime = LocalDateTime.now() },
            Fixture.createBoard(id = 3, title = "board3").apply { meetingTime = LocalDateTime.now().plusDays(1) }
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
        test("모든 먹팟 조회 성공") {
            // when
            val actual = boardService.findAllMuckpot(CursorPaginationRequest(null, allBoardSize.toLong()))
            // then
            actual.list shouldHaveSize 3
            actual.lastId shouldBe allBoard.last().id
        }

        test("참가자가 6명을 넘어가면 마지막에 외N 명으로 응답한다") {
            // when
            val actual = boardService.findAllMuckpot(CursorPaginationRequest(null, allBoardSize.toLong()))
            // then
            actual.list[0].participants.last().nickName shouldBe "외 3명"
        }

        test("오늘, 내일은 meetingTime 기준으로 생성된다.") {
            // when
            val actual = boardService.findAllMuckpot(CursorPaginationRequest(null, allBoardSize.toLong()))
            // then
            actual.list[1].todayOrTomorrow shouldBe TODAY_KR
            actual.list[2].todayOrTomorrow shouldBe TOMORROW_KR
        }
    }

    context("findBoardDetailAndVisit 성공") {
        val loginUser = UserResponse(2, "user2")
        val board = Fixture.createBoard(
            id = 1,
            title = "board1",
            meetingTime = LocalDateTime.of(2100, 12, 25, 12, 20, 30)
        ).apply {
            createdAt = LocalDateTime.of(2100, 12, 23, 12, 20, 30)
        }
        val participantResponses = listOf(
            ParticipantReadResponse(boardId = 1, userId = 1, nickName = "user1"),
            ParticipantReadResponse(boardId = 1, userId = 2, nickName = "user2"),
            ParticipantReadResponse(boardId = 1, userId = 3, nickName = "user3")
        )
        beforeTest {
            // given
            every { boardRepository.findByIdOrNull(any()) } returns board
            every { participantQuerydslRepository.findByBoardIds(any()) } returns participantResponses
            every { boardQuerydslRepository.findPrevId(any()) } returns null
            every { boardQuerydslRepository.findNextId(any()) } returns null
        }

        test("먹팟 상세조회 성공") {
            // when
            val actual = boardService.findBoardDetailAndVisit(1, loginUser)

            // then
            actual.meetingDate shouldBe "12월 25일 (토)"
            actual.meetingTime shouldBe "오후 12:20"
            actual.createDate shouldBe "2100년 12월 23일"
            actual.status shouldBe MuckPotStatus.IN_PROGRESS.korNm
            actual.participants shouldHaveSize participantResponses.size
        }
    }
})
