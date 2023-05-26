package com.yapp.muckpot.domains.board.entity

import com.yapp.muckpot.common.BaseTimeEntity
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "participant")
class Participant(
    @EmbeddedId
    var participantId: ParticipantId
) : BaseTimeEntity() {
    constructor(user: MuckPotUser, board: Board) : this(ParticipantId(user, board))
}
