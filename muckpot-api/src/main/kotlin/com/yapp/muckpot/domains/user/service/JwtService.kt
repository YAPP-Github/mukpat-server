package com.yapp.muckpot.domains.user.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.muckpot.common.MS
import com.yapp.muckpot.common.constants.ACCESS_TOKEN_BASIC_SECONDS
import com.yapp.muckpot.common.constants.ACCESS_TOKEN_KEEP_SECONDS
import com.yapp.muckpot.common.constants.ACCESS_TOKEN_KEY
import com.yapp.muckpot.common.constants.JWT_LOGOUT_VALUE
import com.yapp.muckpot.common.constants.REFRESH_TOKEN_BASIC_SECONDS
import com.yapp.muckpot.common.constants.REFRESH_TOKEN_KEEP_SECONDS
import com.yapp.muckpot.common.constants.REFRESH_TOKEN_KEY
import com.yapp.muckpot.common.constants.USER_CLAIM
import com.yapp.muckpot.common.constants.USER_EMAIL_CLAIM
import com.yapp.muckpot.common.enums.YesNo
import com.yapp.muckpot.common.utils.CookieUtil
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.exception.MuckPotException
import com.yapp.muckpot.redis.RedisService
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val objectMapper: ObjectMapper,
    private val redisService: RedisService,
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
            val token = CookieUtil.getToken(ACCESS_TOKEN_KEY)
            if (isBlackListToken(token)) {
                throw MuckPotException(UserErrorCode.IS_BLACKLIST_TOKEN)
            }
            val decodedJwt = jwtVerifier.verify(token)
            objectMapper.readValue(decodedJwt.getClaim(USER_CLAIM).asString(), UserResponse::class.java)
        } catch (exception: Exception) {
            log.debug(exception) { exception.message }
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

    fun allTokenClear(): Boolean {
        return try {
            val accessToken = CookieUtil.getToken(ACCESS_TOKEN_KEY)
            val decodedRefreshToken = jwtVerifier.verify(CookieUtil.getToken(REFRESH_TOKEN_KEY))
            val decodedAccessToken = jwtVerifier.verify(accessToken)
            val email = decodedRefreshToken.getClaim(USER_EMAIL_CLAIM).asString()
            redisService.setDataExpireWithNewest(accessToken, JWT_LOGOUT_VALUE, this.getTokenExpirationDuration(decodedAccessToken))
            redisService.deleteData(email)
            true
        } catch (exception: Exception) {
            log.debug(exception) { exception.message }
            false
        }
    }

    private fun isBlackListToken(token: String): Boolean {
        return redisService.getData(token) != null
    }

    private fun getTokenExpirationDuration(decodedJwt: DecodedJWT): Long {
        return (decodedJwt.expiresAt.time - Date().time) / MS
    }
}
