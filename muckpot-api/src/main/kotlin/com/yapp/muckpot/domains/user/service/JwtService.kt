package com.yapp.muckpot.domains.user.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.muckpot.common.JwtCookieUtil
import com.yapp.muckpot.common.enums.YesNo
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val objectMapper: ObjectMapper,
    @Value("\${jwt.issuer}")
    private val issuer: String,
    @Value("\${jwt.secret-key}")
    private val secretKey: String
) {
    private val log = KLogging().logger
    private val algorithm: Algorithm by lazy { Algorithm.HMAC512(secretKey) }
    private val jwtVerifier: JWTVerifier by lazy { JWT.require(algorithm).build() }

    fun generateAccessToken(response: UserResponse, keep: YesNo): String {
        val jwtBuilder = JWT.create()
            .withIssuer(issuer)
            .withClaim(USER_CLAIM, objectMapper.writeValueAsString(response))
        if (keep == YesNo.N) {
            jwtBuilder.withExpiresAt(Date(Date().time + ACCESS_EXPIRE_TIME))
        }
        return jwtBuilder.sign(algorithm)
    }

    fun generateRefreshToken(email: String, keep: YesNo): String {
        val jwtBuilder = JWT.create()
            .withIssuer(issuer)
            .withClaim(USER_EMAIL_CLAIM, email)
        if (keep == YesNo.N) {
            jwtBuilder.withExpiresAt(Date(Date().time + REFRESH_EXPIRE_TIME))
        }
        return jwtBuilder.sign(algorithm)
    }

    /**
     * 현재 HttpServletRequest의 AccessToken을 찾아 유저 정보 반환
     *
     * @return UserResponse? 유저 정보가 없는 경우 null 반환
     */
    fun getCurrentUserClaim(): UserResponse? {
        return try {
            val decodedJwt = verify(JwtCookieUtil.getAccessToken())
            objectMapper.readValue(decodedJwt.getClaim(USER_CLAIM).asString(), UserResponse::class.java)
        } catch (exception: Exception) {
            log.debug(exception) { "" }
            log.info { "로그인 유저 정보를 찾을 수 없습니다." }
            null
        }
    }

    private fun verify(token: String): DecodedJWT {
        return jwtVerifier.verify(token)
    }

    companion object {
        private const val USER_CLAIM = "user"
        private const val USER_EMAIL_CLAIM = "email"
        private const val ACCESS_EXPIRE_TIME = 3600000 // 1시간
        private const val REFRESH_EXPIRE_TIME = 604800000 // 일주일
    }
}
