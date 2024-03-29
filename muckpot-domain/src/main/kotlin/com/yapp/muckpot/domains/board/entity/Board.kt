package com.yapp.muckpot.domains.board.entity

import com.yapp.muckpot.common.BaseTimeEntity
import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.constants.AGE_EXP_MSG
import com.yapp.muckpot.common.constants.AGE_MAX
import com.yapp.muckpot.common.constants.AGE_MIN
import com.yapp.muckpot.common.constants.MAX_APPLY_MIN
import com.yapp.muckpot.common.enums.State
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.domains.user.enums.MuckPotStatus.DONE
import com.yapp.muckpot.domains.user.enums.MuckPotStatus.IN_PROGRESS
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "board")
@Where(clause = "state = \'ACTIVE\'")
@SQLDelete(sql = "UPDATE board SET state = 'INACTIVE' WHERE board_id = ?")
class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", referencedColumnName = "user_id")
    var user: MuckPotUser,

    @Column(name = "title", nullable = false)
    var title: String,

    @Embedded
    var location: Location,

    @Column(name = "location_detail")
    var locationDetail: String? = null,

    @Column(name = "meeting_time", nullable = false)
    var meetingTime: LocalDateTime,

    @Column(name = "content")
    var content: String? = "",

    @Column(name = "views")
    var views: Int = 0,

    @Column(name = "current_apply")
    var currentApply: Int = 0,

    @Column(name = "max_apply", nullable = false)
    var maxApply: Int = 2,

    @Column(name = "chat_link", nullable = false)
    var chatLink: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    var status: MuckPotStatus = IN_PROGRESS,

    @Column(name = "min_age")
    var minAge: Int = AGE_MIN,

    @Column(name = "max_age")
    var maxAge: Int = AGE_MAX,

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    var state: State = State.ACTIVE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "province_id")
    var province: Province
) : BaseTimeEntity() {
    init {
        require(minAge in AGE_MIN..AGE_MAX) { AGE_EXP_MSG }
        require(maxAge in AGE_MIN..AGE_MAX) { AGE_EXP_MSG }
        require(minAge <= maxAge) { "최대 나이는 최소 나이 이상이어야 합니다." }
        require(maxApply >= MAX_APPLY_MIN) { "최대 인원은 ${MAX_APPLY_MIN}명 이상 가능합니다." }
        if (isOutOfDate()) {
            this.status = DONE
        }
    }

    fun join(userAge: Int) {
        require(userAge in minAge..maxAge) { "참여 가능 나이가 아닙니다." }
        require(currentApply < maxApply) { "참여 모집이 마감되었습니다." }
        require(status == IN_PROGRESS) { "참여 모집이 마감되었습니다." }
        this.currentApply++
        if (isFull()) {
            this.status = DONE
        }
    }

    fun visit() {
        this.views++
    }

    fun getX(): Double {
        return this.location.locationPoint.x
    }

    fun getY(): Double {
        return this.location.locationPoint.y
    }

    fun isNotMyBoard(userId: Long): Boolean {
        return this.user.id != userId
    }

    fun changeStatus(changeStatus: MuckPotStatus) {
        require(
            (this.status == IN_PROGRESS && changeStatus == DONE) ||
                (this.status == DONE && changeStatus == IN_PROGRESS)
        ) { "변경 가능한 상태가 아닙니다." }
        validateToday()
        this.status = changeStatus
    }

    fun cancelJoin() {
        validateToday()
        this.status = IN_PROGRESS
        this.currentApply--
    }

    fun isNotAgeLimit(): Boolean {
        return (this.minAge == AGE_MIN && this.maxAge == AGE_MAX)
    }

    fun isDone(): Boolean {
        return this.status == DONE
    }

    fun isFull(): Boolean {
        return this.currentApply == this.maxApply
    }

    fun isOutOfDate(): Boolean {
        return meetingTime < LocalDateTime.now()
    }

    private fun validateToday() {
        require(meetingTime > LocalDateTime.now()) { "이미 마감된 먹팟입니다." }
    }
}
