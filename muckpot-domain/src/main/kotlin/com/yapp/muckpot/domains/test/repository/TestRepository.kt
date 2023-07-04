package com.yapp.muckpot.domains.test.repository

import com.yapp.muckpot.domains.test.entity.TestEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRepository : JpaRepository<TestEntity, Long>
