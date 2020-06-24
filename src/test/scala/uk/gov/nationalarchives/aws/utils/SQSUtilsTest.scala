package uk.gov.nationalarchives.aws.utils

import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.{DeleteMessageRequest, DeleteMessageResponse, SendMessageRequest, SendMessageResponse}
import org.scalatest.matchers.should.Matchers._

class SQSUtilsTest extends AnyFlatSpec with MockitoSugar {

  "The send method" should "be called with the correct parameters" in {
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
}