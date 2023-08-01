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
    private val cityRepository: CityRepository,
    private val provinceRepository: ProvinceRepository,
    private val jpaQueryFactory: JPAQueryFactory
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
            Fixture.createBoard(title = "board2", user = user, province = province, status = MuckPotStatus.DONE).apply { createdAt = LocalDateTime.now().plusDays(1) },
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
        val result = boardQuerydslRepository.findAllWithPaginationAndRegion(null, countPerScroll.toLong(), null, null)
        // then
        result shouldHaveSize countPerScroll
    }

    "진행중 먹팟부터 생성일자 기준 내림차순 정렬" {
        // when
        val result = boardQuerydslRepository.findAllWithPaginationAndRegion(null, 3, null, null)
        // then
        result[0].id shouldBe boards[2].id
        result[1].id shouldBe boards[0].id
        result[2].id shouldBe boards[1].id
    }

    "이전 아이디는 진행중 먹팟부터 생성일자 기준 내림차순 정렬 후, 한칸 높은 순위(직후 생성 된)의 번호이다." {
        // when
        val todayPrev = boardQuerydslRepository.findPrevAndNextId(boards[0].id!!, null, null).first
        val tomorrowPrev = boardQuerydslRepository.findPrevAndNextId(boards[1].id!!, null, null).first
        val twoDaysLaterPrev = boardQuerydslRepository.findPrevAndNextId(boards[2].id!!, null, null).first
        // then
        todayPrev shouldBe boards[2].id
        tomorrowPrev shouldBe boards[0].id
        twoDaysLaterPrev shouldBe null
    }

    "이전 아이디는 진행중 먹팟부터 생성일자 기준 내림차순 정렬 후, 한칸 낮은 순위(먼저 생성 된)의 번호이다." {
        // when
        val todayNext = boardQuerydslRepository.findPrevAndNextId(boards[0].id!!, null, null).second
        val tomorrowNext = boardQuerydslRepository.findPrevAndNextId(boards[1].id!!, null, null).second
        val twoDaysLaterNext = boardQuerydslRepository.findPrevAndNextId(boards[2].id!!, null, null).second
        // then
        todayNext shouldBe boards[1].id
        tomorrowNext shouldBe null
        twoDaysLaterNext shouldBe boards[0].id
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

    "먹팟에 등록된 모든 지역정보 조회" {
        val actual = boardQuerydslRepository.findAllRegions()

        actual shouldHaveSize 3
    }

    "지역정보를 한번의 쿼리로 가져온다." {
        val actual = boardQuerydslRepository.findByIdOrNullWithRegion(boards[0].id)!!

        actual.province?.name shouldBe province.name
        actual.province?.city?.name shouldBe city.name
    }

    "DONE 상태의 먹팟을 나중에 조회한다." {
        val actual = boardQuerydslRepository.findAllWithPaginationAndRegion(null, 10, null, null)

        actual[2].status shouldBe MuckPotStatus.DONE
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
