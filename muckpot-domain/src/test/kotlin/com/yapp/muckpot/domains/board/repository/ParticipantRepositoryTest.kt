package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.LocationType
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@CustomDataJpaTest
class ParticipantRepositoryTest(
    @Autowired val muckPotUserRepository: MuckPotUserRepository,
    @Autowired val boardRepository: BoardRepository,
    @Autowired val participantRepository: ParticipantRepository
) : StringSpec({

    "Participant 데이터 저장 성공" {
        // given
        val location = Location("location", 40.7128, -74.0060)
        val user = MuckPotUser(
            null, "email@email.com", "pw", "nickname", Gender.MEN,
            2000, "main", "sub", location, "url"
        )
        val board = Board(
            null, user, "title", location, null, LocationType.COMPANY,
            LocalDateTime.now(), "content", 0, 0, 3, "link",
            MuckPotStatus.IN_PROGRESS, 21, 23
        )
        muckPotUserRepository.save(user)
        boardRepository.save(board)
        // when
        val saveParticipant = participantRepository.save(Participant(user, board))
        // then
        saveParticipant.createdAt shouldNotBe null
    }
})
