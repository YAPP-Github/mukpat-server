package com.yapp.muckpot.filter

import com.yapp.muckpot.common.ResponseWriter
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFailHandler : AuthenticationEntryPoint {
    private val log = KLogging().logger

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.debug(authException) { NO_AUTH }
        ResponseWriter.writeResponse(response, HttpStatus.FORBIDDEN, NO_AUTH)
    }

    companion object {
        private const val NO_AUTH = "권한이 존재하지 않습니다."
    }
}
