package com.yapp.muckpot.email
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService(private val javaMailSender: JavaMailSender) {

    @Value("\${spring.mail.username}")
    private lateinit var from: String

    private val log = KLogging().logger

    // TODO: 동기, 비동기 관련된 부분 좀 더 고민해보기
    @Async
    fun sendAuthMail(authKey: String, to: String) {
        try {
            val subject = EmailTemplates.AUTH_EMAIL_SUBJECT
            val text = EmailTemplates.AUTH_EMAIL_TEXT
            val textSetting = text.formatText(authKey)

            val email = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(email, true, "UTF-8")
            helper.setTo(to)
            helper.setFrom(from)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(textSetting, true)
            javaMailSender.send(email)
        } catch (e: MailException) {
            log.error { "Failed to send email: ${e.message}" }
        }
    }
}
