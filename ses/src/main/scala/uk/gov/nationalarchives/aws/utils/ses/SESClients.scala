package uk.gov.nationalarchives.aws.utils.ses

import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient

import java.net.URI

object SESClients {

  def ses(endpoint: String): SesClient = {
    val httpClient = ApacheHttpClient.builder.build
    SesClient.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpoint))
      .httpClient(httpClient)
      .build()
  }
}
