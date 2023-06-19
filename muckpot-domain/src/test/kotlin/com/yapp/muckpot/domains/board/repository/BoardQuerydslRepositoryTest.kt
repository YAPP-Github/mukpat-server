package com.yapp.muckpot.domains.board.repository

import Fixture
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import io.kotest.core.spec.style.StringSpec
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

    val firstCreated = LocalDateTime.now()
    val secondCreated = LocalDateTime.now().minusDays(1)
    val thirdCreated = LocalDateTime.now().minusDays(2)

    beforeEach {
        user = Fixture.createUser()
        boards = listOf(
            Fixture.createBoard(title = "board1", user = user).apply { createdAt = firstCreated },
            Fixture.createBoard(title = "board2", user = user).apply { createdAt = thirdCreated },
            Fixture.createBoard(title = "board3", user = user).apply { createdAt = secondCreated }
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
})
