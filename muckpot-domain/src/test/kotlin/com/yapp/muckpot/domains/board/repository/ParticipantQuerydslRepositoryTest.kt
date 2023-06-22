package com.yapp.muckpot.domains.board.repository

import Fixture
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

@CustomDataJpaTest
class ParticipantQuerydslRepositoryTest(
    private val participantRepository: ParticipantRepository,
    private val userRepository: MuckPotUserRepository,
    private val boardRepository: BoardRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : StringSpec({
    val participantQuerydslRepository = ParticipantQuerydslRepository(jpaQueryFactory)

    lateinit var users: List<MuckPotUser>
    lateinit var boards: List<Board>
    lateinit var participants: List<Participant>

    beforeEach {
        users = listOf(
            Fixture.createUser(),
            Fixture.createUser(),
            Fixture.createUser()
        )
        boards = listOf(
            Fixture.createBoard(user = users[0]),
            Fixture.createBoard(title = "board2", user = users[0]),
            Fixture.createBoard(title = "board3", user = users[0])
        )
        participants = listOf(
            Fixture.createParticipant(users[0], boards[0]),
            Fixture.createParticipant(users[1], boards[0]),
            Fixture.createParticipant(users[0], boards[1]),
            Fixture.createParticipant(users[0], boards[2])
                .apply { createdAt = LocalDateTime.now().minusDays(3) }
        )
        userRepository.saveAll(users)
        boardRepository.saveAll(boards)
        participantRepository.saveAll(participants)
    }

    afterEach {
        participantRepository.deleteAll()
        boardRepository.deleteAll()
        userRepository.deleteAll()
    }

    "먹팟_ID 리스트를 조건으로 조회 성공" {
        val boardIds = listOf(boards[0].id, boards[2].id)
        // when
        val actual = participantQuerydslRepository.findByBoardIds(boardIds)
        // then
        actual shouldHaveSize 3
    }

    "참여자 목록은 빠른 순서 정렬" {
        val boardIds = listOf(boards[0].id, boards[2].id)
        // when
        val actual = participantQuerydslRepository.findByBoardIds(boardIds).first()
        // then
        actual.boardId shouldBe participants[3].board.id
        actual.userId shouldBe participants[3].user.id
    }
})
