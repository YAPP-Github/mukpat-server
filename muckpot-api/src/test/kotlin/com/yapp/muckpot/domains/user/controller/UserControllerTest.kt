// package com.yapp.muckpot.domains.user.controller
//
// import com.fasterxml.jackson.databind.ObjectMapper
// import com.yapp.muckpot.common.constants.ACCESS_TOKEN_KEY
// import com.yapp.muckpot.common.constants.JWT_LOGOUT_VALUE
// import com.yapp.muckpot.common.enums.YesNo
// import com.yapp.muckpot.domains.user.controller.dto.LoginRequest
// import com.yapp.muckpot.domains.user.entity.MuckPotUser
// import com.yapp.muckpot.domains.user.repository.MuckPotUserRepository
// import com.yapp.muckpot.redis.RedisService
// import io.kotest.core.spec.style.StringSpec
// import io.kotest.matchers.shouldBe
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
// import org.springframework.boot.test.context.SpringBootTest
// import org.springframework.http.MediaType
// import org.springframework.security.crypto.password.PasswordEncoder
// import org.springframework.test.web.servlet.MockMvc
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
// import javax.servlet.http.Cookie
//
// @SpringBootTest
// @AutoConfigureMockMvc
// class UserControllerTest @Autowired constructor(
//    private val mockMvc: MockMvc,
//    private val passwordEncoder: PasswordEncoder,
//    private val userRepository: MuckPotUserRepository,
//    private val redisService: RedisService,
//    private val objectMapper: ObjectMapper
// ) : StringSpec({
//    val pw = "abcd1234"
//    val user = Fixture.createUser(password = passwordEncoder.encode(pw))
//    val loginRequest = LoginRequest(
//        email = user.email,
//        password = pw,
//        keep = YesNo.N
//    )
//    lateinit var loginUser: MuckPotUser
//    lateinit var loginCookies: Array<Cookie>
//    lateinit var accessToken: String
//
//    fun loginAndInit() {
//        loginUser = userRepository.save(user)
//        val response = mockMvc.perform(
//            post("/api/v2/users/login")
//                .content(objectMapper.writeValueAsBytes(loginRequest))
//                .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(
//            status().isOk
//        ).andReturn().response
//
//        loginCookies = response.cookies
//        accessToken = response.getCookie(ACCESS_TOKEN_KEY)?.value!!
//    }
//
//    beforeTest {
//        loginAndInit()
//    }
//
//    afterTest {
//        userRepository.delete(loginUser)
//    }
//
//    "로그아웃 성공 - 리프레시 토큰 삭제, 블랙리스트 추가 확인" {
//        // when
//        mockMvc.perform(
//            post("/api/v1/users/logout")
//                .cookie(*loginCookies)
//        ).andExpect(
//            status().isNoContent
//        )
//        // then
//        val blackList = redisService.getData(accessToken)
//        val refreshToken = redisService.getData(loginUser.email)
//
//        blackList shouldBe JWT_LOGOUT_VALUE
//        refreshToken shouldBe null
//    }
// })
