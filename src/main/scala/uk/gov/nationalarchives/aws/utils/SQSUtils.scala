package uk.gov.nationalarchives.aws.utils

import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.{DeleteMessageRequest, DeleteMessageResponse, SendMessageRequest, SendMessageResponse}

class SQSUtils(sqsClient: SqsClient) {

  def send(queueUrl: String, messageBody: String): SendMessageResponse = {
    sqsClient.sendMessage(SendMessageRequest.builder()
      .queueUrl(queueUrl)
      .messageBody(messageBody)
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
}

object SQSUtils {
  def apply(sqsClient: SqsClient): SQSUtils = new SQSUtils(sqsClient)
}
