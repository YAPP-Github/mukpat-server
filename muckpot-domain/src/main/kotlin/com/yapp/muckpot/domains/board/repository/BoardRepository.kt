package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<Board, Long> {
    fun findByStatus(status: MuckPotStatus): List<Board>
}
