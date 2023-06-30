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
    private val jpaQueryFactory: JPAQueryFactory,
) : StringSpec({
    val boardQuerydslRepository = BoardQuerydslRepository(jpaQueryFactory)

    lateinit var user: MuckPotUser
    lateinit var boards: List<Board>

    val today = LocalDateTime.now()
    val aDayAgo = LocalDateTime.now().minusDays(1)
    val twoDaysAgo = LocalDateTime.now().minusDays(2)

    beforeEach {
        user = Fixture.createUser()
        boards = listOf(
            Fixture.createBoard(title = "board1", user = user).apply { createdAt = today },
            Fixture.createBoard(title = "board2", user = user).apply { createdAt = twoDaysAgo },
            Fixture.createBoard(title = "board3", user = user).apply { createdAt = aDayAgo }
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

    "첫번째 글은 이전 아이디 null" {
        // given
        val boardSeq = 0
        // when
        val prevId = boardQuerydslRepository.findPrevId(boards[boardSeq].id!!)
        val nextId = boardQuerydslRepository.findNextId(boards[boardSeq].id!!)
        // then
        prevId shouldBe null
        nextId shouldBe boards[boardSeq + 1].id
    }

    "이전, 이후 아이디 모두 존재하는 경우" {
        // given
        val boardSeq = 1
        // when
        val prevId = boardQuerydslRepository.findPrevId(boards[boardSeq].id!!)
        val nextId = boardQuerydslRepository.findNextId(boards[boardSeq].id!!)
        // then
        prevId shouldBe boards[boardSeq - 1].id
        nextId shouldBe boards[boardSeq + 1].id
    }

    "마지막 글은 다음 아이디 null" {
        // given
        val boardSeq = 2
        // when
        val prevId = boardQuerydslRepository.findPrevId(boards[boardSeq].id!!)
        val nextId = boardQuerydslRepository.findNextId(boards[boardSeq].id!!)
        // then
        prevId shouldBe boards[boardSeq - 1].id
        nextId shouldBe null
    }

    "이전, 이후 아이디가 없는 경우" {
        // given
        boardRepository.delete(boards[0])
        boardRepository.delete(boards[2])
        // when
        val prevId = boardQuerydslRepository.findPrevId(boards[1].id!!)
        val nextId = boardQuerydslRepository.findNextId(boards[1].id!!)
        // then
        prevId shouldBe null
        nextId shouldBe null
    }

    "이전, 이후 아이디는 IN_PROGRESS 인것만 조회해야 한다." {
        // given
        val board4 = Fixture.createBoard(title = "board4", user = user)
        boards[0].status = MuckPotStatus.DONE
        boards[2].status = MuckPotStatus.DONE
        boardRepository.save(boards[0])
        boardRepository.save(boards[2])
        boardRepository.save(board4)
        // when
        val prevId = boardQuerydslRepository.findPrevId(boards[1].id!!)
        val nextId = boardQuerydslRepository.findNextId(boards[1].id!!)
        // then
        prevId shouldBe null
        nextId shouldBe board4.id
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
