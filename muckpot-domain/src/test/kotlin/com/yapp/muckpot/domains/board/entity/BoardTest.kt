package com.yapp.muckpot.domains.board.entity

import Fixture
import com.yapp.muckpot.common.constants.MAX_APPLY_MIN
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class BoardTest : FunSpec({
    context("Board 유효성 검사") {
        test("minAge는 maxAge 보다 작아야 한다.") {
            shouldThrow<IllegalArgumentException> {
                Fixture.createBoard(
                    minAge = 25,
                    maxAge = 21
                )
            }.message shouldBe "최대 나이는 최소 나이 이상이어야 합니다."
        }

        test("최대 참여인원은 2명 이상이어야 한다.") {
            shouldThrow<IllegalArgumentException> {
                Fixture.createBoard(
                    maxApply = 1
                )
            }.message shouldBe "최대 인원은 ${MAX_APPLY_MIN}명 이상 가능합니다."
        }

        test("현재시간 미만의 먹팟은 DONE으로 생성된다.") {
            val board = Fixture.createBoard(meetingTime = LocalDateTime.now().minusMinutes(10))

            board.status shouldBe MuckPotStatus.DONE
        }

        test("정원이 초과된 경우 참여할 수 없다.") {
            val board = Fixture.createBoard(
                currentApply = 1,
                maxApply = 2
            )
            board.join(23)
            shouldThrow<IllegalArgumentException> {
                board.join(23)
            }.message shouldBe "참여 모집이 마감되었습니다."
        }

        test("나이 제한에 걸릴 경우 참여할 수 없다.") {
            val board = Fixture.createBoard(
                minAge = 21,
                maxAge = 25
            )
            shouldThrow<IllegalArgumentException> {
                board.join(40)
            }.message shouldBe "참여 가능 나이가 아닙니다."
        }

        test("IN_PROGRESS 일 때는 DONE 으로만 변경할 수 있다.") {
            val board = Fixture.createBoard(status = MuckPotStatus.IN_PROGRESS)

            shouldThrow<IllegalArgumentException> {
                board.changeStatus(MuckPotStatus.IN_PROGRESS)
            }.message shouldBe "변경 가능한 상태가 아닙니다."
        }

        test("DONE 일 때는 IN_PROGRESS 으로만 변경할 수 있다.") {
            val board = Fixture.createBoard(status = MuckPotStatus.DONE)

            shouldThrow<IllegalArgumentException> {
                board.changeStatus(MuckPotStatus.DONE)
            }.message shouldBe "변경 가능한 상태가 아닙니다."
        }

        test("모집인원이 마감된 경우에도 IN_PROGRESS 로 변경할 수 있다.") {
            val board = Fixture.createBoard(
                status = MuckPotStatus.DONE,
                currentApply = 3,
                maxApply = 3
            )
            // when
            board.changeStatus(MuckPotStatus.IN_PROGRESS)
            // then
            board.status shouldBe MuckPotStatus.IN_PROGRESS
        }

        test("IN_PROGRESS -> DONE 변경 성공") {
            val board = Fixture.createBoard()
            board.changeStatus(MuckPotStatus.DONE)

            board.status shouldBe MuckPotStatus.DONE
        }

        test("DONE -> IN_PROGRESS 변경 성공") {
            val board = Fixture.createBoard(status = MuckPotStatus.DONE)
            board.changeStatus(MuckPotStatus.IN_PROGRESS)

            board.status shouldBe MuckPotStatus.IN_PROGRESS
        }

        test("현재 시간 이전의 먹팟은 상태를 변경할 수 없다.") {
            val board = Fixture.createBoard(status = MuckPotStatus.DONE)
                .apply { meetingTime = LocalDateTime.now().minusMinutes(30) }

            shouldThrow<IllegalArgumentException> {
                board.changeStatus(MuckPotStatus.IN_PROGRESS)
            }.message shouldBe "이미 마감된 먹팟입니다."
        }

        test("먹팟 취소시 상태 IN_PROGRESS 변경 성공") {
            // given
            val board = Fixture.createBoard(maxApply = 2, currentApply = 2, status = MuckPotStatus.DONE)
            // when
            board.cancelJoin()
            // then
            board.currentApply shouldBe 1
            board.status shouldBe MuckPotStatus.IN_PROGRESS
        }
    }
})
