package com.yapp.muckpot.domains.user.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yapp.muckpot.domains.user.entity.MuckPotUser

data class UserResponse(
    val userId: Long,
    val nickName: String = "",
    @JsonIgnore
    var tokenExpired: Boolean = false
) {
    companion object {
        fun of(user: MuckPotUser): UserResponse {
            return UserResponse(user.id ?: 0, user.nickName)
        }

        fun expiredUser(): UserResponse {
            return UserResponse(-1, tokenExpired = true)
        }
    }
}
