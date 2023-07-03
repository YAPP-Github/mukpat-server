package com.yapp.muckpot.domains.board.repository

import Fixture
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

@CustomDataJpaTest
class BoardQuerydslRepositoryTest(
    private val userRepository: MuckPotUserRepository,
    private val boardRepository: BoardRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : StringSpec({
    val boardQuerydslRepository = BoardQuerydslRepository(jpaQueryFactory)

    lateinit var user: MuckPotUser
    lateinit var boards: List<Board>

    val todaySeq = 0
    val twoDaysAgoSeq = 1
    val aDayAgoSeq = 2

    beforeEach {
        user = Fixture.createUser()
        boards = listOf(
            Fixture.createBoard(title = "board1", user = user).apply { createdAt = LocalDateTime.now() },
            Fixture.createBoard(title = "board2", user = user).apply { createdAt = LocalDateTime.now().minusDays(2) },
            Fixture.createBoard(title = "board3", user = user).apply { createdAt = LocalDateTime.now().minusDays(1) }
        )

        userRepository.save(user)
        boardRepository.saveAll(boards)
    }

    afterEach {
        boardRepository.deleteAll()
        userRepository.deleteAll()
    }

    "countPerScroll이 2인 경우" {
        val countPerScroll = 2
        // when
        val result = boardQuerydslRepository.findAllWithPagination(null, countPerScroll.toLong())
        // then
        result shouldHaveSize countPerScroll
    }

    "생성일자 기준 내림차순 정렬" {
        // when
        val result = boardQuerydslRepository.findAllWithPagination(null, 3)
        // then
        result.last().id shouldBe boards[1].id
    }

    "이전 아이디는 현재 게시글 이후에 등록된 첫번째 글이다." {
        // when
        val todayPrev = boardQuerydslRepository.findPrevId(boards[todaySeq].id!!)
        val aDayAgoPrev = boardQuerydslRepository.findPrevId(boards[aDayAgoSeq].id!!)
        val twoDayAgoPrev = boardQuerydslRepository.findPrevId(boards[twoDaysAgoSeq].id!!)
        // then
        todayPrev shouldBe null
        aDayAgoPrev shouldBe boards[todaySeq].id
        twoDayAgoPrev shouldBe boards[aDayAgoSeq].id
    }

    "다음 아이디는 현재 게시글 이전에 등록된 마지막 글이다." {
        // when
        val todayNext = boardQuerydslRepository.findNextId(boards[todaySeq].id!!)
        val aDayAgoNext = boardQuerydslRepository.findNextId(boards[aDayAgoSeq].id!!)
        val twoDayAgoNext = boardQuerydslRepository.findNextId(boards[twoDaysAgoSeq].id!!)
        // then
        todayNext shouldBe boards[aDayAgoSeq].id
        aDayAgoNext shouldBe boards[twoDaysAgoSeq].id
        twoDayAgoNext shouldBe null
    }

    "현재시간 미만의 먹팟 상태 업데이트" {
        // given
        boards[0].apply { meetingTime = LocalDateTime.now().minusDays(1) }
        boards[1].apply { meetingTime = LocalDateTime.now().minusDays(1) }
        boards[2].apply { meetingTime = LocalDateTime.now().plusDays(1) }
        boardRepository.saveAll(boards)
        // when
        boardQuerydslRepository.updateLessThanCurrentTime()
        // then
        val actual = boardRepository.findByStatus(MuckPotStatus.IN_PROGRESS)
        actual shouldHaveSize 1
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
