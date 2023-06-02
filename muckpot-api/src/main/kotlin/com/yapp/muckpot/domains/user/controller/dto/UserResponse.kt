package com.yapp.muckpot.domains.user.controller.dto

import com.yapp.muckpot.domains.user.entity.MuckPotUser

data class UserResponse(
    val userId: Long,
    val nickName: String = ""
) {
    companion object {
        fun of(user: MuckPotUser): UserResponse {
            return UserResponse(user.id ?: 0, user.nickName)
        }
    }
}
