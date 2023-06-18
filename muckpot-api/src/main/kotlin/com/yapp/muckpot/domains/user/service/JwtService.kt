package com.yapp.muckpot.domains.user.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.muckpot.common.ACCESS_TOKEN_BASIC_SECONDS
import com.yapp.muckpot.common.ACCESS_TOKEN_KEEP_SECONDS
import com.yapp.muckpot.common.JwtCookieUtil
import com.yapp.muckpot.common.MS
import com.yapp.muckpot.common.REFRESH_TOKEN_BASIC_SECONDS
import com.yapp.muckpot.common.REFRESH_TOKEN_KEEP_SECONDS
import com.yapp.muckpot.common.USER_CLAIM
import com.yapp.muckpot.common.USER_EMAIL_CLAIM
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

    fun generateAccessToken(response: UserResponse, expiredSeconds: Long): String {
        val jwtBuilder = JWT.create()
            .withIssuer(issuer)
            .withClaim(USER_CLAIM, objectMapper.writeValueAsString(response))
            .withExpiresAt(Date(Date().time + expiredSeconds * MS))
        return jwtBuilder.sign(algorithm)
    }

    fun generateRefreshToken(email: String, expiredSeconds: Long): String {
        val jwtBuilder = JWT.create()
            .withIssuer(issuer)
            .withClaim(USER_EMAIL_CLAIM, email)
            .withExpiresAt(Date(Date().time + expiredSeconds * MS))
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
            log.debug(exception) { "로그인 유저 정보를 찾을 수 없습니다." }
            null
        }
    }

    fun getAccessTokenSeconds(keep: YesNo): Long {
        return if (keep == YesNo.Y) {
            ACCESS_TOKEN_KEEP_SECONDS
        } else {
            ACCESS_TOKEN_BASIC_SECONDS
        }
    }

    fun getRefreshTokenSeconds(keep: YesNo): Long {
        return if (keep == YesNo.Y) {
            REFRESH_TOKEN_KEEP_SECONDS
        } else {
            REFRESH_TOKEN_BASIC_SECONDS
        }
    }

    private fun verify(token: String): DecodedJWT {
        return jwtVerifier.verify(token)
    }
}
