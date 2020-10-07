package uk.gov.nationalarchives.aws.utils

import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.{Body, Content, Destination, Message, SendEmailRequest, SendEmailResponse}
import uk.gov.nationalarchives.aws.utils.SESUtils.Email

import scala.util.Try

class SESUtils(sesClient: SesClient) {

  def sendEmail(email: Email): Try[SendEmailResponse] = Try {
    val destination = Destination.builder.toAddresses(email.toAddress).build
    val messageContent = Content.builder.charset("UTF-8").data(email.html).build
    val subjectContent = Content.builder.charset("UTF-8").data(email.subject).build
    val messageBody = Body.builder.html(messageContent).build
    val message = Message.builder.body(messageBody).subject(subjectContent).build
    val request = SendEmailRequest.builder
      .destination(destination)
      .message(message)
      .source(email.fromAddress)
      .build

    sesClient.sendEmail(request)
  }
}

object SESUtils {
  case class Email(fromAddress: String, toAddress: String, subject: String, html: String)

  def apply(sesClient: SesClient): SESUtils = new SESUtils(sesClient)
}
