package com.yapp.muckpot.email

data class EmailDto(
    val subject: String,
    val body: String,
    val to: String
)
