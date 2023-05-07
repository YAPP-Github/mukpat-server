package com.yapp.web1be.test

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TestEntity(
    @Id
    val id: Long,
    val name: String
)
