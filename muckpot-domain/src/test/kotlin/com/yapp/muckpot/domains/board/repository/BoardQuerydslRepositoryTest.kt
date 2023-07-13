package com.yapp.muckpot.domains.board.repository

import Fixture
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.City
import com.yapp.muckpot.domains.board.entity.Province
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
    private val cityRepository: CityRepository,
    private val provinceRepository: ProvinceRepository
) : StringSpec({
    val boardQuerydslRepository = BoardQuerydslRepository(jpaQueryFactory)

    lateinit var user: MuckPotUser
    lateinit var boards: List<Board>
    lateinit var province: Province
    lateinit var city: City

    beforeEach {
        user = Fixture.createUser()
        city = cityRepository.save(Fixture.createCity())
        province = provinceRepository.save(Fixture.createProvince(city = city))
        boards = listOf(
            Fixture.createBoard(title = "board1", user = user, province = province).apply { createdAt = LocalDateTime.now() },
            Fixture.createBoard(title = "board2", user = user, province = province).apply { createdAt = LocalDateTime.now().plusDays(1) },
            Fixture.createBoard(title = "board3", user = user, province = province).apply { createdAt = LocalDateTime.now().plusDays(2) }
        )

        userRepository.save(user)
        boardRepository.saveAll(boards)
    }

    afterEach {
        boardRepository.deleteAll()
        userRepository.deleteAll()
        provinceRepository.deleteAll()
        cityRepository.deleteAll()
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
        result[0].id shouldBe boards[2].id
        result[1].id shouldBe boards[1].id
        result[2].id shouldBe boards[0].id
    }

    "이전 아이디는 현재 게시글 이후에 등록된 첫번째 글이다." {
        // when
        val todayPrev = boardQuerydslRepository.findPrevId(boards[0].id!!)
        val tomorrowPrev = boardQuerydslRepository.findPrevId(boards[1].id!!)
        val twoDaysLaterPrev = boardQuerydslRepository.findPrevId(boards[2].id!!)
        // then
        todayPrev shouldBe boards[1].id
        tomorrowPrev shouldBe boards[2].id
        twoDaysLaterPrev shouldBe null
    }

    "다음 아이디는 현재 게시글 이전에 등록된 마지막 글이다." {
        // when
        val todayNext = boardQuerydslRepository.findNextId(boards[0].id!!)
        val tomorrowNext = boardQuerydslRepository.findNextId(boards[1].id!!)
        val twoDaysLaterNext = boardQuerydslRepository.findNextId(boards[2].id!!)
        // then
        todayNext shouldBe null
        tomorrowNext shouldBe boards[0].id
        twoDaysLaterNext shouldBe boards[1].id
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
