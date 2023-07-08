package com.yapp.muckpot.domains.board.repository

import com.yapp.muckpot.domains.board.entity.City
import org.springframework.data.jpa.repository.JpaRepository

interface CityRepository : JpaRepository<City, Long> {
    fun findByName(name: String): City?
}
