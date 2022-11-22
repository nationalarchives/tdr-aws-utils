package uk.gov.nationalarchives.aws.utils.ses

import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.{SendEmailRequest, SendEmailResponse}
import org.scalatest.matchers.should.Matchers._
import uk.gov.nationalarchives.aws.utils.ses.SESUtils.Email

import scala.jdk.CollectionConverters._

class SESUtilsTest extends AnyFlatSpec with MockitoSugar {
  "The sendEmail method" should "be called with the correct parameters" in {
    val sesClient = Mockito.mock(classOf[SesClient])
    val sesUtils = SESUtils(sesClient)
    val argumentCaptor: ArgumentCaptor[SendEmailRequest] = ArgumentCaptor.forClass(classOf[SendEmailRequest])
    val mockResponse = SendEmailResponse.builder.build

    doAnswer(() => mockResponse).when(sesClient).sendEmail(argumentCaptor.capture())
    sesUtils.sendEmail(Email("fromaddress", "toaddress", "subject", "html"))
    val request: SendEmailRequest = argumentCaptor.getValue
    request.destination().toAddresses.asScala.head should equal("toaddress")
    request.source() should equal("fromaddress")
    request.message().body().html().data() should equal("html")
    request.message().subject().data() should equal("subject")
  }
}
