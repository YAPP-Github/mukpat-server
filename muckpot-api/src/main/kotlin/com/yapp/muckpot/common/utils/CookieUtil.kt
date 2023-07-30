package com.yapp.muckpot.common.utils

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
    private const val COOKIE_DOMAIN = ".mukpat.com"

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
    fun addHttpOnlyCookie(name: String, value: String, expiredSeconds: Int?) {
        val cookie = Cookie(name, value).apply {
            path = "/"
            isHttpOnly = true
            domain = COOKIE_DOMAIN
        }
        if (expiredSeconds != null) {
            cookie.apply { maxAge = expiredSeconds }
        }
        // TODO 서버가 분리되면 해당 로직은 제거, (로컬에서 접근을 위한 설정)
        currentRequest.getHeader("Origin")?.let { originName ->
            if (originName.startsWith("http://localhost:")) {
                cookie.domain = "localhost"
            }
        }
        currentResponse.addCookie(cookie)
    }
}
