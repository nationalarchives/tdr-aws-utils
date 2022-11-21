package uk.gov.nationalarchives.aws.utils.sqs

import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient

import java.net.URI

object SQSClients {
  def sqs(endpoint: String): SqsClient = {
    val httpClient = ApacheHttpClient.builder.build
    SqsClient.builder()
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpoint))
      .httpClient(httpClient)
      .build()
  }
}
