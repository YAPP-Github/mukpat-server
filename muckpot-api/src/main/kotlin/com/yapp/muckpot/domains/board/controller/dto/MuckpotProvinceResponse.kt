package com.yapp.muckpot.domains.board.controller.dto

data class MuckpotProvinceResponse(
    val provinceId: Long,
    val provinceName: String,
    val sumByProvince: Int = 0
)
