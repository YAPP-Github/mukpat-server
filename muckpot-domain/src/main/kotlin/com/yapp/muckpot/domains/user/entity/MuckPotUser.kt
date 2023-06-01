package com.yapp.muckpot.domains.user.entity

import com.yapp.muckpot.common.BaseTimeEntity
import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.common.enums.RegexPattern
import com.yapp.muckpot.common.enums.State
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "muckpot_user")
@Entity
class MuckPotUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,

    @Column(name = "email", unique = true)
    val email: String,

    @Column(name = "password")
    var password: String,

    @Column(name = "nick_name", unique = true)
    var nickName: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender")
    val gender: Gender,

    @Column(name = "year_of_birth")
    var yearOfBirth: Int,

    @Column(name = "main_category")
    var mainCategory: String,

    @Column(name = "sub_category")
    var subCategory: String? = null,

    @Embedded
    var location: Location,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    var state: State = State.ACTIVE
) : BaseTimeEntity() {
    init {
        require(email.isNotBlank()) { "이메일은 필수입니다" }
        require(RegexPattern.EMAIL.matches(this.email)) { "유효한 이메일 형식이 아닙니다" }
        require(password.isNotBlank()) { "비밀번호는 필수입니다" }
        require(nickName.isNotBlank()) { "닉네임은 필수입니다" }
        require(yearOfBirth in 1900..2023) { "잘못된 출생 연도입니다" }
        require(mainCategory.isNotBlank()) { "주요 카테고리는 필수입니다" }
    }
}
