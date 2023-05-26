package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.entity.ParticipantId
import org.springframework.data.jpa.repository.JpaRepository

interface ParticipantRepository : JpaRepository<Participant, ParticipantId>
