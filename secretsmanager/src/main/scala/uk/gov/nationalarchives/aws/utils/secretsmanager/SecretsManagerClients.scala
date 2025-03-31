package uk.gov.nationalarchives.aws.utils.secretsmanager

import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient

import java.net.URI


object SecretsManagerClients {
  def secretsmanager(endpoint: String): SecretsManagerClient = {
    val httpClient = ApacheHttpClient.builder.build
    SecretsManagerClient.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpoint))
      .httpClient(httpClient)
      .build()
  }
}
