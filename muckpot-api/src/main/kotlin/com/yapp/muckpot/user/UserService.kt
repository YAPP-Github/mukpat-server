package com.yapp.muckpot.user

import com.yapp.muckpot.common.RandomCodeUtil.generateRandomCode
import com.yapp.muckpot.email.EmailService
import com.yapp.muckpot.redis.RedisService
import com.yapp.muckpot.user.dto.SendEmailAuthRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val emailService: EmailService,
    private val redisService: RedisService
) {

    @Transactional
    fun sendEmailAuth(request: SendEmailAuthRequest) {
        val authKey = generateRandomCode() // 인증 코드 생성
        emailService.sendAuthMail(authKey = authKey, to = request.email!!) // 인증 코드 메일 전송
        redisService.setDataExpireWithNewest(key = request.email!!, value = authKey, 60 * 30L) // 유효기간 30분 redis 저장
    }
}