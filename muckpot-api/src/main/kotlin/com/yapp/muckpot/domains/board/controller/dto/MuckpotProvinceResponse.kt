package com.yapp.muckpot.domains.board.controller.dto

import java.io.Serializable

data class MuckpotProvinceResponse(
    val provinceId: Long = 0,
    val provinceName: String = "",
    val sumByProvince: Int = 0
) : Serializable
