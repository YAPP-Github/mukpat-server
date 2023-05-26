package com.yapp.muckpot.domains.user.repository

import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.config.CustomDataJpaTest
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired

@CustomDataJpaTest
class MuckPotUserRepositoryTest(
    @Autowired val muckPotUserRepository: MuckPotUserRepository
) : StringSpec({
    "Point 타입 저장 성공" {
        // given
        val muckPotUser = MuckPotUser(
            null, "email2@email.com", "pw", "nickname2", Gender.MEN,
            2000, "main", "sub", Location("location", 40.7128, -74.0060)
        )
        // when
        val saveUser = muckPotUserRepository.save(muckPotUser)
        // then
        saveUser.id shouldNotBe null
    }
})
