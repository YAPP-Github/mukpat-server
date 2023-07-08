package com.yapp.muckpot.domains.board.entity

import com.yapp.muckpot.common.BaseTimeEntity
import com.yapp.muckpot.common.enums.State
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "city")
@Where(clause = "state = \'ACTIVE\'")
@SQLDelete(sql = "UPDATE city SET state = 'INACTIVE' WHERE city_id = ?")
class City(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    val id: Long? = null,

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    var state: State = State.ACTIVE,

    @Column(name = "name")
    val name: String

) : BaseTimeEntity() {
    init {
        require(name.isNotBlank()) { "name은 필수입니다" }
    }
}
