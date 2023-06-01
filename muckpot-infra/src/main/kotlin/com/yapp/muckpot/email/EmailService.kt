package com.yapp.muckpot.email
import mu.KLogging
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailService(private val javaMailSender: JavaMailSender) {

    private val log = KLogging().logger

    @Async
    fun sendAuthMail(authKey: String, to: String) {
        try {
            val subject = EmailTemplates.AUTH_EMAIL_SUBJECT
            val text = EmailTemplates.AUTH_EMAIL_TEXT
            val textSetting = text.formatText(authKey)

            val email = javaMailSender.createMimeMessage()
            val helper = MimeMessageHelper(email, true, "UTF-8")
            helper.setTo(to)
            helper.setFrom("muckpot@gmail.com")
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(textSetting, true)
            javaMailSender.send(email)
        } catch (e: MailException) {
            log.error { "Failed to send email: ${e.message}" }
        }
    }
}
