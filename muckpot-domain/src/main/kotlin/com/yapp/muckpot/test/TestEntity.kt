package com.yapp.muckpot.test

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TestEntity(
    @Id
    val id: Long = 1,
    val name: String = "test"
)
