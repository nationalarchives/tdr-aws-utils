package uk.gov.nationalarchives.aws.utils.sqs

import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model._

class SQSUtils(sqsClient: SqsClient) {

  /**
   * Method to send message to SQS queue
   * @param queueUrl
   * Url of the SQS queue
   *
   * @param messageBody
   * Message to send to the SQS queue
   *
   * @param messageGroupId
   * Optional Id used to group messages.
   *
   * @return
   * SendMessageResponse object
   * */
  def send(queueUrl: String, messageBody: String, messageGroupId: Option[String] = None): SendMessageResponse = {
    val requestBuilder = SendMessageRequest.builder()
      .queueUrl(queueUrl)
      .messageBody(messageBody)
      .delaySeconds(0)

    if (messageGroupId.nonEmpty) {
      requestBuilder.messageGroupId(messageGroupId.get)
    }

    sqsClient.sendMessage(requestBuilder.build())
  }

  def delete(queueUrl: String, receiptHandle: String): DeleteMessageResponse = {
    sqsClient.deleteMessage(
      DeleteMessageRequest.builder()
        .queueUrl(queueUrl)
        .receiptHandle(receiptHandle)
        .build())
  }

  def makeMessageVisible(queueUrl: String, receiptHandle: String): ChangeMessageVisibilityResponse = {
    sqsClient.changeMessageVisibility(
      ChangeMessageVisibilityRequest.builder
        .queueUrl(queueUrl)
        .receiptHandle(receiptHandle)
        .visibilityTimeout(0)
        .build
    )
  }
}

object SQSUtils {
  def apply(sqsClient: SqsClient): SQSUtils = new SQSUtils(sqsClient)
}
