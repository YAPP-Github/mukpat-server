package com.yapp.muckpot.filter

import com.yapp.muckpot.common.constants.EMAIL_REQUEST_URL
import com.yapp.muckpot.common.constants.EMAIL_VERIFY_URL
import com.yapp.muckpot.common.constants.LOGIN_URL
import com.yapp.muckpot.common.constants.REISSUE_JWT_URL
import com.yapp.muckpot.common.constants.SIGN_UP_URL
import com.yapp.muckpot.common.enums.StatusCode
import com.yapp.muckpot.common.security.AuthenticationUser
import com.yapp.muckpot.common.utils.ResponseWriter
import com.yapp.muckpot.domains.user.service.JwtService
import org.springframework.http.HttpMethod
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
        jwtService.getCurrentUserClaim()?.let { userResponse ->
            if (ONLY_NON_LOGIN_USER_URLS.contains(request.requestURI.toString())) {
                if (userResponse.tokenExpired) {
                    filterChain.doFilter(request, response)
                    return
                }
                ResponseWriter.writeResponse(response, HttpStatus.BAD_REQUEST.value(), "이미 로그인한 유저 입니다.")
                return
            }
            if (userResponse.tokenExpired) {
                if (needAccessTokenExpiredCheck(request.method.toString(), request.requestURI)) {
                    ResponseWriter.writeResponse(response, StatusCode.INVALID_TOKEN.code, "만료된 토큰입니다.")
                    return
                } else {
                    // 인증정보를 set 하지 않고 패스
                    filterChain.doFilter(request, response)
                    return
                }
            }
            val authentication = AuthenticationUser(userResponse.userId, userResponse, true, LOGIN_USER_AUTHORITIES)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    private fun needAccessTokenExpiredCheck(requestMethod: String, requestUri: String): Boolean {
        return (requestMethod != HttpMethod.GET.toString() && !TOKEN_EXPIRED_NOT_CHECK_URLS.contains(requestUri))
    }

    companion object {
        private val LOGIN_USER_AUTHORITIES = listOf(SimpleGrantedAuthority("ROLE_USER"))
        private val ONLY_NON_LOGIN_USER_URLS = listOf(
            LOGIN_URL,
            SIGN_UP_URL,
            EMAIL_REQUEST_URL,
            EMAIL_VERIFY_URL
        )
        private val TOKEN_EXPIRED_NOT_CHECK_URLS = listOf(
            REISSUE_JWT_URL
        )
    }
}
