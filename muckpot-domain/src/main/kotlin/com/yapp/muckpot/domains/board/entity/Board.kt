package com.yapp.muckpot.domains.board.entity

import com.yapp.muckpot.common.BaseTimeEntity
import com.yapp.muckpot.common.Location
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.LocationType
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
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

    @Column(name = "title")
    val title: String,

    @Embedded
    var location: Location,

    @Column(name = "location_detail")
    var locationDetail: String? = null,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "location_type")
    var locationType: LocationType = LocationType.COMPANY,

    @Column(name = "meeting_time")
    var meetingTime: LocalDateTime,

    @Column(name = "content")
    var content: String? = "",

    @Column(name = "views")
    var views: Int = 0,

    @Column(name = "current_apply")
    var currentApply: Int = 0,

    @Column(name = "max_apply")
    var maxApply: Int = 0,

    @Column(name = "chat_link")
    var chatLink: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    var status: MuckPotStatus = MuckPotStatus.IN_PROGRESS,

    @Column(name = "min_age")
    var minAge: Int = AGE_MIN,

    @Column(name = "max_age")
    var maxAge: Int = AGE_MAX
) : BaseTimeEntity() {
    init {
        require(minAge in AGE_MIN..AGE_MAX) { AGE_EXP_MSG }
        require(maxAge in AGE_MIN..AGE_MAX) { AGE_EXP_MSG }
        require(minAge < maxAge) { "최소나이는 최대나이보다 작아야 합니다" }
    }

    companion object {
        private const val AGE_MIN = 20
        private const val AGE_MAX = 100
        private const val AGE_EXP_MSG = "나이는 $AGE_MIN ~ $AGE_MAX 범위로 입력해주세요."
    }
}
