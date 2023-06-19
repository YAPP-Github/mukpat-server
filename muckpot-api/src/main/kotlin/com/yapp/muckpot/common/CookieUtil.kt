package com.yapp.muckpot.common

import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.exception.MuckPotException
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.util.WebUtils
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Cookie 접근 위한 공통 클래스
 */
object CookieUtil {
    private val currentRequest: HttpServletRequest
        get() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
    private val currentResponse: HttpServletResponse
        get() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).response
            ?: throw IllegalStateException("HttpServletResponse 가 존재하지 않습니다.")

    /**
     * 쿠키에서 토큰값 추출
     *
     * @param tokenName 쿠키에 저장된 토큰이름
     * @exception MuckPotException 토큰 정보를 찾을 수 없는 경우
     */
    fun getToken(tokenName: String): String {
        val cookie = WebUtils.getCookie(currentRequest, tokenName)
        return cookie?.value ?: throw MuckPotException(UserErrorCode.NOT_FOUND_TOKEN)
    }

    /**
     * 현재 응답에 HttpOnly=true 쿠키 추가.
     */
    fun addHttpOnlyCookie(name: String, value: String, expiredSeconds: Int) {
        currentResponse.addCookie(
            Cookie(name, value).apply {
                path = "/"
                isHttpOnly = true
                maxAge = expiredSeconds
            }
        )
    }
}
