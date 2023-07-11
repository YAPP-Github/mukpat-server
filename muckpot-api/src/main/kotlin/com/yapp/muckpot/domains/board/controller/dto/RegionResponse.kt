package com.yapp.muckpot.domains.board.controller.dto

import java.io.Serializable

data class RegionResponse(
    val list: List<MuckpotCityResponse> = emptyList()
) : Serializable
