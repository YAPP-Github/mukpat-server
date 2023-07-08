package com.yapp.muckpot.email

interface EmailService {
    fun sendMail(subject: String, body: String, to: String)
}
