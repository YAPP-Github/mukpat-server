package com.yapp.muckpot.email

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfig(
    @Value("\${spring.mail.host}")
    private val host: String,

    @Value("\${spring.mail.port}")
    private val port: Int,

    @Value("\${spring.mail.username}")
    private val username: String,

    @Value("\${spring.mail.password}")
    private val password: String
) {

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl().apply {
            host = this@EmailConfig.host
            port = this@EmailConfig.port
            username = this@EmailConfig.username
            password = this@EmailConfig.password
        }

        val javaMailProperties = Properties() // TLS 사용 연결
        javaMailProperties["mail.smtp.auth"] = "true"
        javaMailProperties["mail.smtp.starttls.enable"] = "true"
        mailSender.javaMailProperties = javaMailProperties

        return mailSender
    }
}
