package com.yapp.muckpot.email

interface EmailService {
    fun sendAuthMail(authKey: String, to: String)
}
