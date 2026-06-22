package uk.gov.nationalarchives.aws.utils.ses

import software.amazon.awssdk.http.apache5.Apache5HttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient

import java.net.URI

object SESClients {

  def ses(endpoint: String): SesClient = {
    val httpClient = Apache5HttpClient.builder.build
    SesClient.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpoint))
      .httpClient(httpClient)
      .build()
  }
}
