package com.yapp.muckpot.filter

import com.yapp.muckpot.common.utils.ResponseWriter
import com.yapp.muckpot.domains.user.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtLogoutHandler(
    private val jwtService: JwtService
) : LogoutHandler {
    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        if (!jwtService.allTokenClear()) {
            ResponseWriter.writeResponse(response, HttpStatus.BAD_REQUEST, "로그아웃 실패.")
        }
    }
}
