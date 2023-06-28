package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.common.redisson.ConcurrencyHelper
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotUpdateRequest
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.domains.board.exception.ParticipantErrorCode
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.ParticipantRepository
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.exception.MuckPotException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
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
    val createRequest = MuckpotCreateRequest(
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
        chatLink = "chatLink"
    )
    val updateRequest = MuckpotUpdateRequest(
        meetingDate = LocalDate.now(),
        meetingTime = LocalTime.of(12, 0),
        maxApply = 6,
        minAge = 25,
        maxAge = 70,
        locationName = "modify location",
        locationDetail = "detail",
        x = 1.0,
        y = 1.0,
        title = "modify title",
        content = "content",
        chatLink = "modify chatLink"
    )

    beforeEach {
        user = userRepository.save(Fixture.createUser())
        userId = user.id!!
    }

    afterEach {
        participantRepository.deleteAll()
        boardRepository.deleteAll()
        userRepository.deleteAll()
    }

    "먹팟 생성 성공" {
        // when
        val boardId = boardService.saveBoard(userId, createRequest)

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        val participant = participantRepository.findByBoard(findBoard)

        findBoard shouldNotBe null
        findBoard.user.id shouldBe userId
        findBoard.currentApply shouldBe 1
        participant shouldNotBe null
    }

    "자신의 글은 조회수가 증가하지 않는다." {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        val loginUserInfo = UserResponse.of(user)

        // when
        boardService.findBoardDetailAndVisit(boardId, loginUserInfo)
        boardService.findBoardDetailAndVisit(boardId, loginUserInfo)

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        findBoard.views shouldBe 0
    }

    "먹팟 상세 조회시 조회수가 증가한다." {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        val otherUser = UserResponse.of(userRepository.save(Fixture.createUser()))

        // when
        boardService.findBoardDetailAndVisit(boardId, otherUser)

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        findBoard.views shouldBe 1
    }

    "비로그인 유저도 조회수가 증가한다." {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        // when
        boardService.findBoardDetailAndVisit(boardId, null)
        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        findBoard.views shouldBe 1
    }

    "먹팟 수정 성공" {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        // when
        boardService.updateBoard(userId, boardId, updateRequest)
        // then
        val actual = boardRepository.findByIdOrNull(boardId)!!
        actual.maxApply shouldBe updateRequest.maxApply
        actual.minAge shouldBe updateRequest.minAge
        actual.maxAge shouldBe updateRequest.maxAge
        actual.location.locationName shouldBe updateRequest.locationName
        actual.getX() shouldBe updateRequest.x
        actual.getY() shouldBe updateRequest.y
        actual.title shouldBe updateRequest.title
        actual.content shouldBe updateRequest.content
        actual.chatLink shouldBe updateRequest.chatLink
    }

    "자신의 글만 수정할 수 있다." {
        // given
        val otherUserId = -1L
        val boardId = boardService.saveBoard(userId, createRequest)!!
        // when & then
        shouldThrow<MuckPotException> {
            boardService.updateBoard(otherUserId, boardId, updateRequest)
        }.errorCode shouldBe BoardErrorCode.BOARD_UNAUTHORIZED
    }

    "먹팟 참가 신청 성공" {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
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
        val participant = participantRepository.findByBoard(findBoard)
        findBoard.currentApply shouldBe 2
        participant shouldNotBe null
    }

    "먹팟 중복 참가 신청 불가 검증" {
        val boardId = boardService.saveBoard(userId, createRequest)!!
        shouldThrow<MuckPotException> {
            boardService.joinBoard(userId, boardId)
        }.errorCode shouldBe ParticipantErrorCode.ALREADY_JOIN
    }

    "먹팟 참가 동시성 테스트" {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
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

    "먹팟 삭제 요청시 INACTIVE로 변경된다." {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!

        // when
        boardService.deleteBoard(userId, boardId)

        val findBoard = boardRepository.findByIdOrNull(boardId)
        findBoard shouldBe null
    }

    "자신의 글만 삭제할 수 있다." {
        // given
        val otherUserId = -1L
        val boardId = boardService.saveBoard(userId, createRequest)!!
        // when & then
        shouldThrow<MuckPotException> {
            boardService.deleteBoard(otherUserId, boardId)
        }.errorCode shouldBe BoardErrorCode.BOARD_UNAUTHORIZED
    }

    "글 삭제시 참여자 목록도 함께 삭제한다." {
        // given
        val board = boardRepository.save(Fixture.createBoard(user = user))
        // when
        boardService.deleteBoard(userId, board.id!!)
        // then
        val findBoard = participantRepository.findByBoard(board)
        findBoard shouldHaveSize 0
    }

    "먹팟 상태변경 성공" {
        // given
        val boardId = boardRepository.save(Fixture.createBoard(user = user)).id!!
        // when
        boardService.changeStatus(userId, boardId, MuckPotStatus.DONE)
        // then
        val actual = boardRepository.findByIdOrNull(boardId)!!
        actual.status shouldBe MuckPotStatus.DONE
    }

    "먹팟 참가 신청 취소 성공" {
        // given
        val applyUser = userRepository.save(Fixture.createUser())
        val board = boardRepository.save(Fixture.createBoard(user = user))
        participantRepository.save(Participant(applyUser, board))
        // when
        boardService.cancelJoin(applyUser.id!!, board.id!!)
        // then
        val findParticipant = participantRepository.findByUserAndBoard(applyUser, board)
        findParticipant shouldBe null
        board.currentApply shouldBe 0
    }

    "기존 참가 신청 내역 없으면 참가 신청 취소 불가" {
        // given
        val applyUser = userRepository.save(Fixture.createUser())
        val board = boardRepository.save(Fixture.createBoard(user = user))
        // when & then
        shouldThrow<MuckPotException> {
            boardService.cancelJoin(applyUser.id!!, board.id!!)
        }.errorCode shouldBe ParticipantErrorCode.PARTICIPANT_NOT_FOUND
    }

    "먹팟 글 작성자는 참가 신청 취소할 수 없다." {
        // given
        val board = boardRepository.save(Fixture.createBoard(user = user))
        participantRepository.save(Participant(user, board))
        // when & then
        shouldThrow<MuckPotException> {
            boardService.cancelJoin(user.id!!, board.id!!)
        }.errorCode shouldBe ParticipantErrorCode.WRITER_MUST_JOIN
    }
})
