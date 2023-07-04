package com.yapp.muckpot.common.utils

import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

object SecurityContextHolderUtil {
    /**
     * SecurityContextHolder 에서 credential 을 가져온다.
     *
     * credential 정보
     * @see com.yapp.muckpot.common.security.AuthenticationUser
     */
    fun getCredentialOrNull(): UserResponse? {
        if (SecurityContextHolder.getContext().authentication is AnonymousAuthenticationToken) {
            return null
        }
        return SecurityContextHolder.getContext().authentication.credentials as UserResponse
    }
}
