package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.domains.board.entity.Board
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<Board, Long>
