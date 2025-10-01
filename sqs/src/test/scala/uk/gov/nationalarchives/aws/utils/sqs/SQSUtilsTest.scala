package uk.gov.nationalarchives.aws.utils.sqs

import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model._

class SQSUtilsTest extends AnyFlatSpec with MockitoSugar {

  "The send method" should "be called with the correct parameters" in {
    val sqsClient = Mockito.mock(classOf[SqsClient])
    val sqsUtils = SQSUtils(sqsClient)
    val argumentCaptor: ArgumentCaptor[SendMessageRequest] = ArgumentCaptor.forClass(classOf[SendMessageRequest])
    val mockResponse = SendMessageResponse.builder.build

    doAnswer(() => mockResponse).when(sqsClient).sendMessage(argumentCaptor.capture())

    sqsUtils.send("testurl", "testbody", Some("messageGroupId"))
    val request: SendMessageRequest = argumentCaptor.getValue
    request.delaySeconds should equal(0)
    request.queueUrl should equal("testurl")
    request.messageBody should equal("testbody")
    request.messageGroupId should equal("messageGroupId")
  }

  "The send method" should "not send message group id when not provided" in {
    val sqsClient = Mockito.mock(classOf[SqsClient])
    val sqsUtils = SQSUtils(sqsClient)
    val argumentCaptor: ArgumentCaptor[SendMessageRequest] = ArgumentCaptor.forClass(classOf[SendMessageRequest])
    val mockResponse = SendMessageResponse.builder.build

    doAnswer(() => mockResponse).when(sqsClient).sendMessage(argumentCaptor.capture())

    sqsUtils.send("testurl", "testbody")
    val request: SendMessageRequest = argumentCaptor.getValue
    request.delaySeconds should equal(0)
    request.queueUrl should equal("testurl")
    request.messageBody should equal("testbody")
    request.messageGroupId shouldBe null
  }

  "The delete method" should "be called with the correct parameters" in {
    val sqsClient = Mockito.mock(classOf[SqsClient])
    val sqsUtils = SQSUtils(sqsClient)
    val argumentCaptor: ArgumentCaptor[DeleteMessageRequest] = ArgumentCaptor.forClass(classOf[DeleteMessageRequest])
    val mockResponse = DeleteMessageResponse.builder.build

    doAnswer(() => mockResponse).when(sqsClient).deleteMessage(argumentCaptor.capture())

    sqsUtils.delete("testurl", "testreceipthandle")
    val request: DeleteMessageRequest = argumentCaptor.getValue
    request.queueUrl should equal("testurl")
    request.receiptHandle() should equal("testreceipthandle")
  }

  "The makeMessageVisible method" should "set the visibility to zero" in {
    val sqsClient = Mockito.mock(classOf[SqsClient])
    val sqsUtils = SQSUtils(sqsClient)
    val argumentCaptor: ArgumentCaptor[ChangeMessageVisibilityRequest] =
      ArgumentCaptor.forClass(classOf[ChangeMessageVisibilityRequest])
    val mockResponse = ChangeMessageVisibilityResponse.builder.build

    doAnswer(() => mockResponse).when(sqsClient).changeMessageVisibility(argumentCaptor.capture())

    sqsUtils.makeMessageVisible("testurl", "testreceipthandle")

    val request: ChangeMessageVisibilityRequest = argumentCaptor.getValue
    request.queueUrl should equal("testurl")
    request.receiptHandle should equal("testreceipthandle")
    request.visibilityTimeout should equal(0)
  }
}
