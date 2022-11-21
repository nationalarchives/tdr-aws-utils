package uk.gov.nationalarchives.aws.utils.kms

import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kms.KmsClient

import java.net.URI

object KMSClients {
  def kms(endpoint: String): KmsClient = {
    val httpClient = ApacheHttpClient.builder.build
    KmsClient.builder()
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpoint))
      .httpClient(httpClient)
      .build()
  }
}
