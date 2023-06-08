package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.fixture.Fixture
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired

@CustomDataJpaTest
class ParticipantRepositoryTest(
    @Autowired val muckPotUserRepository: MuckPotUserRepository,
    @Autowired val boardRepository: BoardRepository,
    @Autowired val participantRepository: ParticipantRepository
) : StringSpec({

    "Participant 데이터 저장 성공" {
        // given
        val user = Fixture.createUser()
        val board = Fixture.createBoard(user = user)
        muckPotUserRepository.save(user)
        boardRepository.save(board)
        // when
        val saveParticipant = participantRepository.save(Participant(user, board))
        // then
        saveParticipant.createdAt shouldNotBe null
    }
})
