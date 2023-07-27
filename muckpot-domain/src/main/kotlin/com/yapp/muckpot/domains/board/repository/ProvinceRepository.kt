package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.domains.board.entity.Province
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProvinceRepository : JpaRepository<Province, Long> {
    @Query("SELECT p FROM Province p JOIN FETCH p.city WHERE p.name = :name")
    fun findByName(name: String): Province?
}
