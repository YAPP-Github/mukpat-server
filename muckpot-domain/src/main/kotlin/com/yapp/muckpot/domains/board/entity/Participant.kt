package com.yapp.muckpot.domains.board.entity

import com.yapp.muckpot.common.BaseTimeEntity
import com.yapp.muckpot.common.enums.State
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Table

@Entity
@Table(name = "participant")
class Participant(
    @EmbeddedId
    var participantId: ParticipantId,

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    var state: State = State.ACTIVE
) : BaseTimeEntity() {
    constructor(user: MuckPotUser, board: Board) : this(ParticipantId(user, board))
}
