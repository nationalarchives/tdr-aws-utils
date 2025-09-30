package uk.gov.nationalarchives.aws.utils.sqs

import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model._

import java.util.UUID

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
   * Id used to group messages. If not supplied will default to random generated UUID
   *
   * @return
   * SendMessageResponse object
   * */
  def send(queueUrl: String, messageBody: String, messageGroupId: Option[String] = None): SendMessageResponse = {
    val groupId = messageGroupId.getOrElse(UUID.randomUUID().toString)
    sqsClient.sendMessage(SendMessageRequest.builder()
      .queueUrl(queueUrl)
      .messageBody(messageBody)
      .messageGroupId(groupId)
      .delaySeconds(0)
      .build())
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
