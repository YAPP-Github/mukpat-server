package com.yapp.muckpot.domains.user.repository

import com.yapp.muckpot.domains.user.entity.MuckPotUser
import org.springframework.data.jpa.repository.JpaRepository

interface MuckPotUserRepository : JpaRepository<MuckPotUser, Long>
