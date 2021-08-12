package uk.gov.nationalarchives.aws.utils

import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{PublishRequest, PublishResponse}

class SNSUtilsTest extends AnyFlatSpec with MockitoSugar  {
  "The publish method" should "be called with the correct parameters" in {
    val testMessage = "test message"
    val testTopicArn = "testTopicArn"
    val snsClient = Mockito.mock(classOf[SnsClient])
    val snsUtils = SNSUtils(snsClient)
    val argumentCaptor: ArgumentCaptor[PublishRequest] = ArgumentCaptor.forClass(classOf[PublishRequest])
    val mockResponse = PublishResponse.builder().build()

    doAnswer(() => mockResponse).when(snsClient).publish(argumentCaptor.capture())

    snsUtils.publish(testMessage, testTopicArn)
    val request: PublishRequest = argumentCaptor.getValue
    request.message should equal(testMessage)
    request.topicArn should equal(testTopicArn)
  }
}
