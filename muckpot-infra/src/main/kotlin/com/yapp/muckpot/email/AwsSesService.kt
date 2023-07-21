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
import org.springframework.transaction.event.TransactionalEventListener

@Service
class AwsSesService(
    private val amazonSimpleEmailService: AmazonSimpleEmailService
) {
    private val log = KLogging().logger

    @Async
    @TransactionalEventListener
    fun sendMail(emailDto: EmailDto) {
        try {
            val destination = Destination().withToAddresses(emailDto.to)
            val message = Message().apply {
                withSubject(Content(emailDto.subject))
                withBody(Body().withHtml(Content(emailDto.body)))
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
