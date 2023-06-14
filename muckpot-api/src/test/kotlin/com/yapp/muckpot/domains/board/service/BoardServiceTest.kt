package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.common.redisson.ConcurrencyHelper
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.entity.ParticipantId
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.ParticipantRepository
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.exception.MuckPotException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicLong

@SpringBootTest
class BoardServiceTest @Autowired constructor(
    private val boardService: BoardService,
    private val boardRepository: BoardRepository,
    private val userRepository: MuckPotUserRepository,
    private val participantRepository: ParticipantRepository
) : StringSpec({
    lateinit var user: MuckPotUser
    var userId: Long = 0
    val request = MuckpotCreateRequest(
        meetingDate = LocalDate.now(),
        meetingTime = LocalTime.of(12, 0),
        maxApply = 70,
        minAge = 20,
        maxAge = 100,
        locationName = "location",
        locationDetail = null,
        x = 0.0,
        y = 0.0,
        title = "title",
        content = null,
        chatLink = ""
    )

    beforeEach {
        user = userRepository.save(
            MuckPotUser(
                null, "test@naver.com", "pw", "nickname",
                Gender.MEN, 2000, JobGroupMain.DEVELOPMENT, "sub", Location("location", 0.0, 0.0), "url"
            )
        )
        userId = user.id!!
    }

    afterEach {
        participantRepository.deleteAll()
        boardRepository.deleteAll()
        userRepository.deleteAll()
    }

    "먹팟 생성 성공" {
        // when
        val boardId = boardService.saveBoard(userId, request)

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        val participant = participantRepository.findById(ParticipantId(user, findBoard))

        findBoard shouldNotBe null
        findBoard.user?.id shouldBe userId
        findBoard.currentApply shouldBe 1
        participant shouldNotBe null
    }

    "먹팟 상세 조회시 조회수가 증가한다." {
        // given
        val boardId = boardService.saveBoard(userId, request)!!
        val loginUserInfo = UserResponse.of(user)

        // when
        boardService.findBoardDetailAndVisit(boardId, loginUserInfo)
        boardService.findBoardDetailAndVisit(boardId, loginUserInfo)

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        findBoard.views shouldBe 2
    }

    "먹팟 참가 신청 성공" {
        // given
        val boardId = boardService.saveBoard(userId, request)!!
        val applyUser = userRepository.save(
            MuckPotUser(
                null, "test1@naver.com", "pw", "nickname1",
                Gender.MEN, 2000, JobGroupMain.DEVELOPMENT, "sub", Location("location", 0.0, 0.0), "url"
            )
        )
        val applyUserId = applyUser.id!!

        // when
        boardService.joinBoard(applyUserId, boardId)

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        val participant = participantRepository.findByIdOrNull(ParticipantId(applyUser, findBoard))
        findBoard.currentApply shouldBe 2
        participant shouldNotBe null
    }

    "먹팟 중복 참가 신청 불가 검증" {
        val boardId = boardService.saveBoard(userId, request)!!
        shouldThrow<MuckPotException> {
            boardService.joinBoard(userId, boardId)
        }.errorCode shouldBe BoardErrorCode.ALREADY_JOIN
    }

    "먹팟 참가 동시성 테스트" {
        // given
        val boardId = boardService.saveBoard(userId, request)!!
        val applyUser = userRepository.save(
            MuckPotUser(
                null, "test1@naver.com", "pw", "nickname1",
                Gender.MEN, 2000, JobGroupMain.DEVELOPMENT, "sub", Location("location", 0.0, 0.0), "url"
            )
        )
        val applyId = applyUser.id!!

        // when
        val successCount = AtomicLong()
        ConcurrencyHelper.execute(
            { boardService.joinBoard(applyId, boardId) },
            successCount
        )

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        findBoard.currentApply shouldBe 2
    }
})
