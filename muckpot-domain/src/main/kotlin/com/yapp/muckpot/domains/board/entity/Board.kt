package com.yapp.muckpot.domains.board.entity

import com.yapp.muckpot.common.AGE_EXP_MSG
import com.yapp.muckpot.common.AGE_MAX
import com.yapp.muckpot.common.AGE_MIN
import com.yapp.muckpot.common.BaseTimeEntity
import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.MAX_APPLY_MIN
import com.yapp.muckpot.common.enums.State
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import java.time.LocalDate
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

@Table(name = "board")
@Entity
class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", referencedColumnName = "user_id")
    var user: MuckPotUser? = null,

    @Column(name = "title", nullable = false)
    val title: String,

    @Embedded
    var location: Location,

    @Column(name = "location_detail")
    var locationDetail: String? = null,

    @Column(name = "meeting_date", nullable = false)
    var meetingDate: LocalDate,

    @Column(name = "meeting_time", nullable = false)
    var meetingTime: String,

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
    var status: MuckPotStatus = MuckPotStatus.IN_PROGRESS,

    @Column(name = "min_age")
    var minAge: Int = AGE_MIN,

    @Column(name = "max_age")
    var maxAge: Int = AGE_MAX,

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    var state: State = State.ACTIVE
) : BaseTimeEntity() {
    init {
        require(minAge in AGE_MIN..AGE_MAX) { AGE_EXP_MSG }
        require(maxAge in AGE_MIN..AGE_MAX) { AGE_EXP_MSG }
        require(minAge < maxAge) { "최소나이는 최대나이보다 작아야 합니다." }
        require(maxApply >= MAX_APPLY_MIN) { "최대 인원은 ${MAX_APPLY_MIN}명 이상 가능합니다." }
        participate()
    }

    fun participate() {
        require(currentApply < maxApply) { "정원이 초과되었습니다." }
        this.currentApply++
    }
}
