package com.yapp.muckpot.email

data class EmailSendEvent(
    val subject: String,
    val body: String,
    val to: String
)
