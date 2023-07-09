package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.domains.board.entity.Province
import org.springframework.data.jpa.repository.JpaRepository

interface ProvinceRepository : JpaRepository<Province, Long> {
    fun findByName(name: String): Province?
}
