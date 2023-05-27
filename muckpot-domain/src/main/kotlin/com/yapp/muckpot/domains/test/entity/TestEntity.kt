package com.yapp.muckpot.domains.test.entity

import com.yapp.muckpot.common.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class TestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long ? = 1,
    val name: String = "test"
) : BaseTimeEntity()
