package com.yapp.muckpot.domains.board.entity

import com.yapp.muckpot.common.BaseTimeEntity
import com.yapp.muckpot.common.enums.State
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "province")
@Where(clause = "state = \'ACTIVE\'")
@SQLDelete(sql = "UPDATE province SET state = 'INACTIVE' WHERE province_id = ?")
class Province(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "province_id")
    val id: Long? = null,

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    var state: State = State.ACTIVE,

    @Column(name = "name")
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", referencedColumnName = "city_id")
    var city: City

) : BaseTimeEntity() {
    init {
        require(name.isNotBlank()) { "name은 필수입니다" }
    }
}
