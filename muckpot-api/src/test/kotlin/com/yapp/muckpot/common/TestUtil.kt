package com.yapp.muckpot.common

import com.yapp.muckpot.common.security.AuthenticationUser
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

object TestUtil {
    /**
     * 테스트 용도, SecurityContextHolder에 직접 객체를 넣어줌
     */
    fun forceLogin(user: UserResponse?) {
        var authentication: AuthenticationUser? = null
        user?.let {
            authentication = AuthenticationUser(
                user.userId,
                UserResponse(user.userId, user.nickName),
                true,
                listOf(
                    SimpleGrantedAuthority("ROLE_USER")
                )
            )
        }
        SecurityContextHolder.getContext().authentication = authentication
    }
}
