package com.yapp.muckpot.redis.dto

import java.io.Serializable

data class MuckpotCityResponse(
    val cityId: Long = 0,
    val cityName: String = "",
    var sumByCity: Int = 0,
    val provinces: List<MuckpotProvinceResponse> = emptyList()
) : Serializable
