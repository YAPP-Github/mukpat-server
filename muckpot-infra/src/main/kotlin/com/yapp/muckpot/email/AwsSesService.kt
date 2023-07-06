package com.yapp.muckpot.email

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import mu.KLogging
import org.springframework.mail.MailException
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AwsSesService(
    private val amazonSimpleEmailService: AmazonSimpleEmailService
) : EmailService {
    private val log = KLogging().logger

    @Async
    override fun sendAuthMail(authKey: String, to: String) {
        try {
            val subject = EmailTemplate.AUTH_EMAIL.subject
            val text = EmailTemplate.AUTH_EMAIL.formatBody(authKey)
            val message = Message()

            message.withSubject(Content(subject))
            message.withBody(Body().withHtml(Content(text)))
            val destination = Destination().withToAddresses(to)
            amazonSimpleEmailService.sendEmail(
                SendEmailRequest()
                    .withSource("noreply@mukpat.com")
                    .withDestination(destination)
                    .withMessage(message)
            )
        } catch (e: MailException) {
            log.error { "Failed to send email: ${e.message}" }
        }
    }

    @Async
    override fun sendMail(subject: String, body: String, to: String) {
        try {
            val destination = Destination().withToAddresses(to)
            val message = Message().apply {
                withSubject(Content(subject))
                withBody(Body().withHtml(Content(body)))
            }

            amazonSimpleEmailService.sendEmail(
                SendEmailRequest()
                    .withSource(SENDER_EMAIL)
                    .withDestination(destination)
                    .withMessage(message)
            )
        } catch (e: MailException) {
            log.error(e) { "Failed to send email: ${e.message}" }
        }
    }

    companion object {
        private const val SENDER_EMAIL = "noreply@mukpat.com"
    }
}
