package com.yapp.muckpot.redis.dto

import java.io.Serializable

data class MuckpotProvinceResponse(
    val provinceId: Long = 0,
    val provinceName: String = "",
    val sumByProvince: Int = 0
) : Serializable
