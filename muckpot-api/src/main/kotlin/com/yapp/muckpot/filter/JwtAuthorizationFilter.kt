package com.yapp.muckpot.filter

import com.yapp.muckpot.common.LOGIN_URL
import com.yapp.muckpot.common.ResponseWriter
import com.yapp.muckpot.common.security.AuthenticationUser
import com.yapp.muckpot.config.SecurityConfig.Companion.POST_PERMIT_ALL_URLS
import com.yapp.muckpot.domains.user.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(private val jwtService: JwtService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        jwtService.getCurrentUserClaim()?.let {
            if (POST_PERMIT_ALL_URLS.contains(request.requestURI.equals(LOGIN_URL).toString())) {
                ResponseWriter.writeResponse(response, HttpStatus.BAD_REQUEST, "이미 로그인한 유저 입니다.")
                return
            }
            val authentication = AuthenticationUser(it.userId, it, true, LOGIN_USER_AUTHORITIES)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private val LOGIN_USER_AUTHORITIES = listOf(SimpleGrantedAuthority("ROLE_USER"))
    }
}
