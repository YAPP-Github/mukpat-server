package com.yapp.muckpot.domains.user.service

import com.yapp.muckpot.common.JwtCookieUtil
import com.yapp.muckpot.domains.user.controller.dto.LoginRequest
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.exception.MuckPotException
import com.yapp.muckpot.redis.RedisService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: MuckPotUserRepository,
    private val jwtService: JwtService,
    private val redisService: RedisService,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun login(request: LoginRequest): UserResponse {
        userRepository.findByEmail(request.email)?.let {
            if (!passwordEncoder.matches(request.password, it.password)) {
                throw MuckPotException(UserErrorCode.LOGIN_FAIL)
            }
            val response = UserResponse.of(it)
            val accessToken = jwtService.generateAccessToken(response, request.keep)
            val refreshToken = jwtService.generateRefreshToken(request.email, request.keep)
            redisService.saveRefreshToken(request.email, refreshToken)
            JwtCookieUtil.addAccessTokenCookie(accessToken)
            JwtCookieUtil.addRefreshTokenCookie(refreshToken)
            return response
        } ?: run {
            throw MuckPotException(UserErrorCode.LOGIN_FAIL)
        }
    }
}
