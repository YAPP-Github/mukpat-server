package com.yapp.muckpot.domains.user.repository

import com.yapp.muckpot.domains.user.entity.MuckPotUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

interface MuckPotUserRepository : JpaRepository<MuckPotUser, Long> {
    @Transactional(readOnly = true)
    fun findByEmail(email: String): MuckPotUser?
}
