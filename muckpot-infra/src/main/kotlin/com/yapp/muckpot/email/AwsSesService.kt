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
            val subject = EmailTemplates.AUTH_EMAIL_SUBJECT
            val text = EmailTemplates.AUTH_EMAIL_TEXT
            val textSetting = text.formatText(authKey)

            val message = Message()
            message.withSubject(Content(subject))
            message.withBody(Body().withHtml(Content(textSetting)))
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
}
