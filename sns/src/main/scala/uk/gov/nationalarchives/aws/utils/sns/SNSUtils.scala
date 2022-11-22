package uk.gov.nationalarchives.aws.utils.sns

import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{PublishRequest, PublishResponse}

class SNSUtils(snsClient: SnsClient) {
  def publish(message: String, topicArn: String): PublishResponse = {
    snsClient.publish(PublishRequest.builder()
      .message(message)
      .topicArn(topicArn)
      .build())
  }
}

object SNSUtils {
  def apply(snsClient: SnsClient): SNSUtils = new SNSUtils(snsClient)
}
