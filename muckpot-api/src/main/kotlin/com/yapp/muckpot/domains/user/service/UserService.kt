package com.yapp.muckpot.domains.user.service

import com.yapp.muckpot.common.JwtCookieUtil
import com.yapp.muckpot.common.RandomCodeUtil
import com.yapp.muckpot.domains.user.controller.dto.EmailAuthResponse
import com.yapp.muckpot.domains.user.controller.dto.LoginRequest
import com.yapp.muckpot.domains.user.controller.dto.SendEmailAuthRequest
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.controller.dto.VerifyEmailAuthRequest
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.email.EmailService
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
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService
) {
    val THIRTY_MINS: Long = 60 * 30L

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

    @Transactional
    fun sendEmailAuth(request: SendEmailAuthRequest): EmailAuthResponse {
        val authKey = RandomCodeUtil.generateRandomCode()
        emailService.sendAuthMail(authKey = authKey, to = request.email)
        redisService.setDataExpireWithNewest(key = request.email, value = authKey, duration = THIRTY_MINS)
        return EmailAuthResponse(authKey)
    }

    @Transactional
    fun verifyEmailAuth(request: VerifyEmailAuthRequest) {
        val authKey = redisService.getData(request.email)
        authKey?.let {
            if (it != request.verificationCode) {
                throw MuckPotException(UserErrorCode.EMAIL_VERIFY_FAIL)
            }
        } ?: run {
            throw MuckPotException(UserErrorCode.NO_VERIFY_CODE)
        }
    }

    fun findLoginUserProfile(): UserResponse? {
        return jwtService.getCurrentUserClaim()
    }
}
