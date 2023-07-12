package com.yapp.muckpot.redis.dto

import java.io.Serializable

data class RegionResponse(
    val list: List<MuckpotCityResponse> = emptyList()
) : Serializable
