package com.yapp.muckpot.domains.board.repository

import Fixture
import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired

@CustomDataJpaTest
class ParticipantRepositoryTest(
    @Autowired val muckPotUserRepository: MuckPotUserRepository,
    @Autowired val boardRepository: BoardRepository,
    @Autowired val participantRepository: ParticipantRepository,
    @Autowired val cityRepository: CityRepository,
    @Autowired val provinceRepository: ProvinceRepository
) : StringSpec({

    afterEach {
        participantRepository.deleteAll()
        boardRepository.deleteAll()
        muckPotUserRepository.deleteAll()
        provinceRepository.deleteAll()
        cityRepository.deleteAll()
    }

    "Participant 데이터 저장 성공" {
        // given
        val user = Fixture.createUser()
        val city = cityRepository.save(Fixture.createCity())
        val province = provinceRepository.save(Fixture.createProvince(city = city))
        val board = Fixture.createBoard(user = user, province = province)
        muckPotUserRepository.save(user)
        boardRepository.save(board)
        // when
        val saveParticipant = participantRepository.save(Participant(user, board))
        // then
        saveParticipant.createdAt shouldNotBe null
    }

    "Participant 데이터 소프트 삭제 성공" {
        // given
        val user = muckPotUserRepository.save(Fixture.createUser())
        val city = cityRepository.save(Fixture.createCity())
        val province = provinceRepository.save(Fixture.createProvince(city = city))
        val board = boardRepository.save(Fixture.createBoard(user = user, province = province))
        participantRepository.save(Participant(user, board))

        // when
        participantRepository.deleteByBoard(board)

        // then
        val actual = participantRepository.findByBoard(board)
        actual shouldHaveSize 0
    }

    "유저와 보드로 조회 할 수 있다." {
        // given
        val user = muckPotUserRepository.save(Fixture.createUser())
        val city = cityRepository.save(Fixture.createCity())
        val province = provinceRepository.save(Fixture.createProvince(city = city))
        val board = boardRepository.save(Fixture.createBoard(user = user, province = province))
        participantRepository.save(Participant(user, board))
        // when
        val actual = participantRepository.findByUserAndBoard(user, board)!!
        // then
        actual.user.id shouldBe user.id
        actual.board.id shouldBe board.id
    }
})
