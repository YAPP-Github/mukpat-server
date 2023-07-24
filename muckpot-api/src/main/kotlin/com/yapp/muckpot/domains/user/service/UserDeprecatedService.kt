package com.yapp.muckpot.domains.user.service

import com.yapp.muckpot.common.constants.ACCESS_TOKEN_KEY
import com.yapp.muckpot.common.constants.REFRESH_TOKEN_KEY
import com.yapp.muckpot.common.utils.CookieUtil
import com.yapp.muckpot.common.utils.RandomCodeUtil
import com.yapp.muckpot.domains.user.controller.dto.EmailAuthResponse
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.controller.dto.deprecated.LoginRequestV1
import com.yapp.muckpot.domains.user.controller.dto.deprecated.SendEmailAuthRequestV1
import com.yapp.muckpot.domains.user.controller.dto.deprecated.SignUpRequestV1
import com.yapp.muckpot.domains.user.controller.dto.deprecated.VerifyEmailAuthRequestV1
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.email.EmailSendEvent
import com.yapp.muckpot.email.EmailTemplate
import com.yapp.muckpot.exception.MuckPotException
import com.yapp.muckpot.redis.RedisService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Deprecated("V2 배포 후 제거")
class UserDeprecatedService(
    private val userRepository: MuckPotUserRepository,
    private val jwtService: JwtService,
    private val redisService: RedisService,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher
) {
    val THIRTY_MINS: Long = 60 * 30L

    @Transactional
    fun signUpV1(request: SignUpRequestV1): UserResponse {
        userRepository.findByEmail(request.email)?.let {
            throw MuckPotException(UserErrorCode.ALREADY_EXISTS_USER)
        } ?: run {
            val jobGroupMain = JobGroupMain.findByKorName(request.jobGroupMain)
            val encodePw = passwordEncoder.encode(request.password)
            val user = userRepository.save(request.toUser(jobGroupMain, encodePw))
            return UserResponse.of(user)
        }
    }

    @Transactional
    fun loginV1(request: LoginRequestV1): UserResponse {
        userRepository.findByEmail(request.email)?.let {
            if (!passwordEncoder.matches(request.password, it.password)) {
                throw MuckPotException(UserErrorCode.LOGIN_FAIL)
            }
            val response = UserResponse.of(it)
            val refreshTokenSeconds = jwtService.getRefreshTokenSeconds(request.keep)
            val accessToken = jwtService.generateAccessToken(response)
            val refreshToken = jwtService.generateRefreshToken(request.email, refreshTokenSeconds)

            redisService.setDataExpireWithNewest(request.email, refreshToken, refreshTokenSeconds)
            CookieUtil.addHttpOnlyCookie(ACCESS_TOKEN_KEY, accessToken)
            CookieUtil.addHttpOnlyCookie(REFRESH_TOKEN_KEY, refreshToken)
            return response
        } ?: run {
            throw MuckPotException(UserErrorCode.LOGIN_FAIL)
        }
    }

    @Transactional
    fun sendEmailAuthV1(request: SendEmailAuthRequestV1): EmailAuthResponse {
        userRepository.findByEmail(request.email)?.let {
            throw MuckPotException(UserErrorCode.ALREADY_EXISTS_USER)
        } ?: run {
            val authKey = RandomCodeUtil.generateRandomCode()
            eventPublisher.publishEvent(
                EmailSendEvent(
                    subject = EmailTemplate.AUTH_EMAIL.subject,
                    body = EmailTemplate.AUTH_EMAIL.formatBody(authKey),
                    to = request.email
                )
            )
            redisService.setDataExpireWithNewest(key = request.email, value = authKey, duration = THIRTY_MINS)
            return EmailAuthResponse(authKey)
        }
    }

    @Transactional
    fun verifyEmailAuthV1(request: VerifyEmailAuthRequestV1) {
        val authKey = redisService.getData(request.email)
        authKey?.let {
            if (it != request.verificationCode) {
                throw MuckPotException(UserErrorCode.EMAIL_VERIFY_FAIL)
            }
        } ?: run {
            throw MuckPotException(UserErrorCode.NO_VERIFY_CODE)
        }
    }
}
