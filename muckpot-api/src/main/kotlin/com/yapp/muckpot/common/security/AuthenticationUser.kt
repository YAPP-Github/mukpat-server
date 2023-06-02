package com.yapp.muckpot.common.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import java.time.LocalDateTime

data class AuthenticationUser(
    val userId: Long,
    val credential: Any,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val authorities: List<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {
    constructor(userId: Long, credential: Any, isAuthentication: Boolean, authorities: List<GrantedAuthority>) :
        this(userId = userId, credential = credential, authorities = authorities) {
        isAuthenticated = isAuthentication
    }

    override fun getPrincipal(): Any {
        return userId
    }

    override fun getCredentials(): Any {
        return credential
    }
}
