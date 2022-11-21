package uk.gov.nationalarchives.aws.utils.sns

import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient

import java.net.URI

object SNSClients {
  def sns(endpointPath: String): SnsClient = {
    val httpClient = ApacheHttpClient.builder.build
    SnsClient.builder()
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpointPath))
      .httpClient(httpClient)
      .build()
  }
}
