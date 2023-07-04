package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface ParticipantRepository : JpaRepository<Participant, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Participant p SET p.state = 'INACTIVE' WHERE p.board = :board")
    fun deleteByBoard(board: Board)

    fun findByBoard(board: Board): List<Participant>

    fun findByUserAndBoard(user: MuckPotUser, board: Board): Participant?
}
