package com.yapp.muckpot.domains.user.service

import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.domains.user.controller.dto.SendEmailAuthRequest
import com.yapp.muckpot.domains.user.controller.dto.SignUpRequest
import com.yapp.muckpot.domains.user.exception.UserErrorCode
import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
import com.yapp.muckpot.exception.MuckPotException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userService: UserService,
    private val userRepository: MuckPotUserRepository
) : StringSpec({

    val request = SignUpRequest(
        email = UUID.randomUUID().toString().substring(0, 5) + "@naver.com",
        password = "abc1234!",
        nickname = UUID.randomUUID().toString().substring(0, 3),
        jobGroupMain = "개발",
        jobGroupSub = null,
        locationName = "삼전 본사",
        x = 0.0,
        y = 0.0,
        gender = Gender.WOMEN,
        yearOfBirth = 1996
    )

    afterEach {
        userRepository.findByEmail(request.email)?.let { userRepository.delete(it) }
    }

    "회원가입 성공" {
        // when
        val user = userService.signUp(request)
        // then
        user shouldNotBe null
        user.nickName shouldBe request.nickname
    }

    "중복 회원가입 불가 검증" {
        userService.signUp(request)

        shouldThrow<MuckPotException> {
            userService.signUp(request)
        }.errorCode shouldBe UserErrorCode.ALREADY_EXISTS_USER
    }

    "인증 메일 받기 단계에서 중복이메일 유효성 검사를 한다." {
        // given
        userService.signUp(request)
        // when & then
        shouldThrow<MuckPotException> {
            userService.sendEmailAuth(SendEmailAuthRequest(request.email))
        }.errorCode shouldBe UserErrorCode.ALREADY_EXISTS_USER
    }
})
