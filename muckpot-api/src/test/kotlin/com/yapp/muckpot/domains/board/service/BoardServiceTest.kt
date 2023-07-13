package com.yapp.muckpot.domains.board.service

import Fixture
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.common.redisson.ConcurrencyHelper
import com.yapp.muckpot.domains.board.controller.dto.MuckpotCreateRequest
import com.yapp.muckpot.domains.board.controller.dto.MuckpotUpdateRequest
import com.yapp.muckpot.domains.board.controller.dto.RegionFilterRequest
import com.yapp.muckpot.domains.board.entity.City
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.entity.Province
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.domains.board.exception.ParticipantErrorCode
import com.yapp.muckpot.domains.board.repository.BoardRepository
import com.yapp.muckpot.domains.board.repository.CityRepository
import com.yapp.muckpot.domains.board.repository.ParticipantRepository
import com.yapp.muckpot.domains.board.repository.ProvinceRepository
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.exception.MuckPotException
import com.yapp.muckpot.redis.RedisService
import com.yapp.muckpot.redis.constants.REGIONS_CACHE_NAME
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
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
    private val participantRepository: ParticipantRepository,
    private val cityRepository: CityRepository,
    private val provinceRepository: ProvinceRepository,
    private val provinceService: ProvinceService,
    private val redisService: RedisService
) : FunSpec({
    lateinit var user: MuckPotUser
    lateinit var province: Province
    lateinit var city: City
    var userId: Long = 0
    val createRequest = MuckpotCreateRequest(
        meetingDate = LocalDate.now().plusDays(1),
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
        chatLink = "chatLink",
        region_1depth_name = "서울특별시",
        region_2depth_name = "강남구"
    )
    val updateRequest = MuckpotUpdateRequest(
        meetingDate = LocalDate.now().plusDays(1),
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
        chatLink = "modify chatLink",
        region_1depth_name = "서울특별시",
        region_2depth_name = "송파구"
    )
    val regionsRedisKey = "$REGIONS_CACHE_NAME::all"

    beforeEach {
        user = userRepository.save(Fixture.createUser())
        userId = user.id!!
        city = cityRepository.save(Fixture.createCity())
        province = provinceRepository.save(Fixture.createProvince(city = city))
    }

    afterEach {
        participantRepository.deleteAll()
        boardRepository.deleteAll()
        userRepository.deleteAll()
        provinceRepository.deleteAll()
        cityRepository.deleteAll()
    }

    test("먹팟 생성 성공") {
        // when
        val boardId = boardService.saveBoard(userId, createRequest)

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        val participant = participantRepository.findByBoard(findBoard)
        val findCity = cityRepository.findByName(createRequest.region_1depth_name)
        val findProvince = provinceRepository.findByName(createRequest.region_2depth_name)

        findBoard shouldNotBe null
        findBoard.user.id shouldBe userId
        findBoard.currentApply shouldBe 1
        participant shouldNotBe null
        findCity shouldNotBe null
        findProvince shouldNotBe null
    }

    test("자신의 글은 조회수가 증가하지 않는다.") {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        val loginUserInfo = UserResponse.of(user)

        // when
        boardService.findBoardDetailAndVisit(boardId, loginUserInfo, RegionFilterRequest())
        boardService.findBoardDetailAndVisit(boardId, loginUserInfo, RegionFilterRequest())

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        findBoard.views shouldBe 0
    }

    test("먹팟 상세 조회시 조회수가 증가한다.") {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        val otherUser = UserResponse.of(userRepository.save(Fixture.createUser()))

        // when
        boardService.findBoardDetailAndVisit(boardId, otherUser, RegionFilterRequest())

        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        findBoard.views shouldBe 1
    }

    test("비로그인 유저도 조회수가 증가한다.") {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        // when
        boardService.findBoardDetailAndVisit(boardId, null, RegionFilterRequest())
        // then
        val findBoard = boardRepository.findByIdOrNull(boardId)!!
        findBoard.views shouldBe 1
    }

    test("먹팟 수정 성공") {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        // when
        boardService.updateBoardAndSendEmail(userId, boardId, updateRequest)
        // then
        val actual = boardRepository.findByIdOrNull(boardId)!!
        val findProvince = provinceRepository.findByName(updateRequest.region_2depth_name)

        actual.maxApply shouldBe updateRequest.maxApply
        actual.minAge shouldBe updateRequest.minAge
        actual.maxAge shouldBe updateRequest.maxAge
        actual.location.locationName shouldBe updateRequest.locationName
        actual.getX() shouldBe updateRequest.x
        actual.getY() shouldBe updateRequest.y
        actual.title shouldBe updateRequest.title
        actual.content shouldBe updateRequest.content
        actual.chatLink shouldBe updateRequest.chatLink
        actual.province!!.id.shouldBe(findProvince!!.id)
    }

    test("자신의 글만 수정할 수 있다.") {
        // given
        val otherUserId = -1L
        val boardId = boardService.saveBoard(userId, createRequest)!!
        // when & then
        shouldThrow<MuckPotException> {
            boardService.updateBoardAndSendEmail(otherUserId, boardId, updateRequest)
        }.errorCode shouldBe BoardErrorCode.BOARD_UNAUTHORIZED
    }

    test("먹팟 참가 신청 성공") {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        val applyUser = userRepository.save(
            MuckPotUser(
                null, "test1@naver.com", "pw", "nickname1",
                Gender.MEN, 2000, JobGroupMain.DEVELOPMENT, "sub", "url"
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

    test("먹팟 중복 참가 신청 불가 검증") {
        val boardId = boardService.saveBoard(userId, createRequest)!!
        shouldThrow<MuckPotException> {
            boardService.joinBoard(userId, boardId)
        }.errorCode shouldBe ParticipantErrorCode.ALREADY_JOIN
    }

    test("먹팟 참가 동시성 테스트") {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!
        val applyUser = userRepository.save(
            MuckPotUser(
                null, "test1@naver.com", "pw", "nickname1",
                Gender.MEN, 2000, JobGroupMain.DEVELOPMENT, "sub", "url"
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

    test("먹팟 삭제 요청시 INACTIVE로 변경된다.") {
        // given
        val boardId = boardService.saveBoard(userId, createRequest)!!

        // when
        boardService.deleteBoardAndSendEmail(userId, boardId)

        val findBoard = boardRepository.findByIdOrNull(boardId)
        findBoard shouldBe null
    }

    test("자신의 글만 삭제할 수 있다.") {
        // given
        val otherUserId = -1L
        val boardId = boardService.saveBoard(userId, createRequest)!!
        // when & then
        shouldThrow<MuckPotException> {
            boardService.deleteBoardAndSendEmail(otherUserId, boardId)
        }.errorCode shouldBe BoardErrorCode.BOARD_UNAUTHORIZED
    }

    test("글 삭제시 참여자 목록도 함께 삭제한다.") {
        // given
        val board = boardRepository.save(Fixture.createBoard(user = user, province = province))
        // when
        boardService.deleteBoardAndSendEmail(userId, board.id!!)
        // then
        val findBoard = participantRepository.findByBoard(board)
        findBoard shouldHaveSize 0
    }

    test("먹팟 상태변경 성공") {
        // given
        val boardId = boardRepository.save(Fixture.createBoard(user = user, province = province)).id!!
        // when
        boardService.changeStatus(userId, boardId, MuckPotStatus.DONE)
        // then
        val actual = boardRepository.findByIdOrNull(boardId)!!
        actual.status shouldBe MuckPotStatus.DONE
    }

    test("먹팟 참가 신청 취소 성공") {
        // given
        val applyUser = userRepository.save(Fixture.createUser())
        val board = boardRepository.save(Fixture.createBoard(user = user, province = province))
        participantRepository.save(Participant(applyUser, board))
        // when
        boardService.cancelJoinAndSendEmail(applyUser.id!!, board.id!!)
        // then
        val findParticipant = participantRepository.findByUserAndBoard(applyUser, board)
        findParticipant shouldBe null
        board.currentApply shouldBe 0
    }

    test("기존 참가 신청 내역 없으면 참가 신청 취소 불가") {
        // given
        val applyUser = userRepository.save(Fixture.createUser())
        val board = boardRepository.save(Fixture.createBoard(user = user, province = province))
        // when & then
        shouldThrow<MuckPotException> {
            boardService.cancelJoinAndSendEmail(applyUser.id!!, board.id!!)
        }.errorCode shouldBe ParticipantErrorCode.PARTICIPANT_NOT_FOUND
    }

    test("먹팟 글 작성자는 참가 신청 취소할 수 없다.") {
        // given
        val board = boardRepository.save(Fixture.createBoard(user = user, province = province))
        participantRepository.save(Participant(user, board))
        // when & then
        shouldThrow<MuckPotException> {
            boardService.cancelJoinAndSendEmail(user.id!!, board.id!!)
        }.errorCode shouldBe ParticipantErrorCode.WRITER_MUST_JOIN
    }

    test("먹팟 생성 시 시/도,군/구 값은 최초 1번만 디비에 값 저장 후 재사용") {
        // when
        val boardId1 = boardService.saveBoard(userId, createRequest)
        val boardId2 = boardService.saveBoard(userId, createRequest)
        // then
        val findBoard1 = boardRepository.findByIdOrNull(boardId1)!!
        val findBoard2 = boardRepository.findByIdOrNull(boardId2)!!
        val findCity = cityRepository.findByName(createRequest.region_1depth_name)
        val findProvince = provinceRepository.findByName(createRequest.region_2depth_name)

        findBoard1 shouldNotBe null
        findBoard1.province!!.id.shouldBe(findBoard2.province!!.id)
        findCity shouldNotBe null
        findProvince shouldNotBe null
    }

    test("지역 조회정보는 최초1회 redis에 저장한다.") {
        // when
        boardService.findAllRegions()

        // then
        val actual = redisService.getData(regionsRedisKey)
        actual shouldNotBe null
    }

    test("먹팟 생성시 지역정보는 redis에서 삭제된다.") {
        // given
        boardService.findAllRegions()

        // when
        boardService.saveBoard(userId, createRequest)

        // then
        val actual = redisService.getData(regionsRedisKey)
        actual shouldBe null
    }

    context("상세 조회 이전, 이후 아이디 테스트") {
        var thirdBoardId: Long = 0
        var secondBoardId: Long = 0
        var firstBoardId: Long = 0

        lateinit var province1: Province

        beforeTest {
            // given
            province1 = provinceService.saveProvinceIfNot("city1", "province1")
            val province2 = provinceService.saveProvinceIfNot("city1", "province2")
            val province3 = provinceService.saveProvinceIfNot("city2", "province3")
            thirdBoardId = boardService.saveBoard(
                userId,
                createRequest.apply {
                    region_1depth_name = province3.city.name
                    region_2depth_name = province3.name
                }
            )!!
            secondBoardId = boardService.saveBoard(
                userId,
                createRequest.apply {
                    region_1depth_name = province2.city.name
                    region_2depth_name = province2.name
                }
            )!!
            firstBoardId = boardService.saveBoard(
                userId,
                createRequest.apply {
                    region_1depth_name = province1.city.name
                    region_2depth_name = province1.name
                }
            )!!
        }

        test("cityId 조건이 포함되는 경우") {
            // when
            val actual = boardService.findBoardDetailAndVisit(
                firstBoardId,
                UserResponse.of(user),
                RegionFilterRequest(
                    cityId = province1.city.id
                )
            )
            // then
            actual.prevId shouldBe null
            actual.nextId shouldBe secondBoardId
        }

        test("cityId, provinceId 조건이 포함되는 경우") {
            // when
            val actual = boardService.findBoardDetailAndVisit(
                firstBoardId,
                UserResponse.of(user),
                RegionFilterRequest(cityId = province1.city.id, provinceId = province1.id)
            )
            // then
            actual.prevId shouldBe null
            actual.nextId shouldBe null
        }

        test("지역 필터 없는경우") {
            // when
            val actual = boardService.findBoardDetailAndVisit(
                secondBoardId,
                UserResponse.of(user),
                RegionFilterRequest()
            )
            // then
            actual.prevId shouldBe firstBoardId
            actual.nextId shouldBe thirdBoardId
        }
    }
})
