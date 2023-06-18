package com.yapp.muckpot.common

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.util.WebUtils
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Jwt Cookie 접근 위한 공통 클래스
 */
object JwtCookieUtil {
    private const val ACCESS_TOKEN = "accessToken"
    private const val REFRESH_TOKEN = "refreshToken"

    private val currentRequest: HttpServletRequest
        get() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
    private val currentResponse: HttpServletResponse
        get() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).response
            ?: throw IllegalStateException("HttpServletResponse 가 존재하지 않습니다.")

    /**
     * 현재 Request 에서 AccessToken 을 추출
     *
     * @exception IllegalArgumentException 쿠키 값이 없는 경우
     */
    fun getAccessToken(): String {
        val cookie = WebUtils.getCookie(currentRequest, ACCESS_TOKEN)
        return cookie?.value ?: throw IllegalArgumentException("로그인 정보가 존재하지 않습니다.")
    }

    /**
     * 현재 응답에 ACCESS_TOKEN 쿠키 추가.
     */
    fun addAccessTokenCookie(accessToken: String, expiredSeconds: Int) {
        currentResponse.addCookie(
            Cookie(ACCESS_TOKEN, accessToken).apply {
                path = "/"
                isHttpOnly = true
                maxAge = expiredSeconds
            }
        )
    }

    /**
     * 현재 응답에 REFRESH_TOKEN 쿠키 추가.
     */
    fun addRefreshTokenCookie(jwtToken: String, expiredSeconds: Int) {
        currentResponse.addCookie(
            Cookie(REFRESH_TOKEN, jwtToken).apply {
                path = "/"
                isHttpOnly = true
                maxAge = expiredSeconds
            }
        )
    }
}
