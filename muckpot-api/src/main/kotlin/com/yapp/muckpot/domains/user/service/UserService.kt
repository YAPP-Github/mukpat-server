package com.yapp.muckpot.domains.user.service

import com.yapp.muckpot.common.constants.ACCESS_TOKEN_KEY
import com.yapp.muckpot.common.constants.ACCESS_TOKEN_SECONDS
import com.yapp.muckpot.common.constants.REFRESH_TOKEN_KEY
import com.yapp.muckpot.common.utils.CookieUtil
import com.yapp.muckpot.common.utils.RandomCodeUtil
import com.yapp.muckpot.domains.user.controller.dto.EmailAuthResponse
import com.yapp.muckpot.domains.user.controller.dto.LoginRequest
import com.yapp.muckpot.domains.user.controller.dto.SendEmailAuthRequest
import com.yapp.muckpot.domains.user.controller.dto.SignUpRequest
import com.yapp.muckpot.domains.user.controller.dto.SignUpRequestV1
import com.yapp.muckpot.domains.user.controller.dto.UserResponse
import com.yapp.muckpot.domains.user.controller.dto.VerifyEmailAuthRequest
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.email.EmailService
import com.yapp.muckpot.email.EmailTemplate
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
            val refreshTokenSeconds = jwtService.getRefreshTokenSeconds(request.keep)
            val accessToken = jwtService.generateAccessToken(response)
            val refreshToken = jwtService.generateRefreshToken(request.email, refreshTokenSeconds)

            redisService.setDataExpireWithNewest(request.email, refreshToken, refreshTokenSeconds)
            CookieUtil.addHttpOnlyCookie(ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_SECONDS.toInt())
            CookieUtil.addHttpOnlyCookie(REFRESH_TOKEN_KEY, refreshToken, refreshTokenSeconds.toInt())
            return response
        } ?: run {
            throw MuckPotException(UserErrorCode.LOGIN_FAIL)
        }
    }

    @Transactional
    fun sendEmailAuth(request: SendEmailAuthRequest): EmailAuthResponse {
        userRepository.findByEmail(request.email)?.let {
            throw MuckPotException(UserErrorCode.ALREADY_EXISTS_USER)
        } ?: run {
            val authKey = RandomCodeUtil.generateRandomCode()
            emailService.sendMail(
                subject = EmailTemplate.AUTH_EMAIL.subject,
                body = EmailTemplate.AUTH_EMAIL.formatBody(authKey),
                to = request.email
            )
            redisService.setDataExpireWithNewest(key = request.email, value = authKey, duration = THIRTY_MINS)
            return EmailAuthResponse(authKey)
        }
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

    @Transactional
    fun signUp(request: SignUpRequest): UserResponse {
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
    fun reissueJwt(refreshToken: String, accessToken: String) {
        if (!jwtService.isTokenExpired(accessToken)) throw MuckPotException(UserErrorCode.FAIL_JWT_REISSUE)
        val email = jwtService.getCurrentUserEmail(refreshToken)
            ?: throw MuckPotException(UserErrorCode.FAIL_JWT_REISSUE)
        val redisToken = redisService.getData(email) ?: throw MuckPotException(UserErrorCode.FAIL_JWT_REISSUE)
        if (redisToken != refreshToken) throw MuckPotException(UserErrorCode.FAIL_JWT_REISSUE)

        userRepository.findByEmail(email)?.let { user ->
            val leftRefreshTokenSeconds = jwtService.getLeftExpirationTime(refreshToken)
            val response = UserResponse.of(user)
            val newAccessToken = jwtService.generateAccessToken(response)
            val newRefreshToken = jwtService.generateNewRefreshFromOldRefresh(user.email, refreshToken)

            redisService.setDataExpireWithNewest(user.email, newRefreshToken, leftRefreshTokenSeconds)
            CookieUtil.addHttpOnlyCookie(ACCESS_TOKEN_KEY, newAccessToken, ACCESS_TOKEN_SECONDS.toInt())
            CookieUtil.addHttpOnlyCookie(REFRESH_TOKEN_KEY, newRefreshToken, leftRefreshTokenSeconds.toInt())
        } ?: run {
            throw MuckPotException(UserErrorCode.USER_NOT_FOUND)
        }
    }

    @Deprecated("V2 배포 후 제거")
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
}
