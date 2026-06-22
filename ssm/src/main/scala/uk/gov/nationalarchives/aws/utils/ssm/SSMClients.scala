package uk.gov.nationalarchives.aws.utils.ssm

import software.amazon.awssdk.http.apache5.Apache5HttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ssm.SsmClient

import java.net.URI

object SSMClients {
  def ssm(endpoint: String): SsmClient = {
    val httpClient = Apache5HttpClient.builder.build
    SsmClient.builder()
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpoint))
      .httpClient(httpClient)
      .build()
  }
}
