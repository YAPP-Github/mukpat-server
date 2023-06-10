package com.yapp.muckpot.domains.user.repository

import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.fixture.Fixture.createUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired

@CustomDataJpaTest
class MuckPotUserRepositoryTest(
    @Autowired val muckPotUserRepository: MuckPotUserRepository
) : StringSpec({
    "Point 타입 저장 성공" {
        // given
        val muckPotUser = createUser()
        // when
        val saveUser = muckPotUserRepository.save(muckPotUser)
        // then
        saveUser.id shouldNotBe null
    }

    "findByEmail 호출 성공" {
        val user = muckPotUserRepository.findByEmail("user@samsung.com")

        user shouldBe null
    }
})
