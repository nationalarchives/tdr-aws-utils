package uk.gov.nationalarchives.aws.utils.ecr

import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ecr.EcrAsyncClient

import java.net.URI

object ECRClients {
  def ecr(endpoint: URI): EcrAsyncClient = {
    val httpClient = NettyNioAsyncHttpClient.builder.build
    EcrAsyncClient.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(endpoint)
      .httpClient(httpClient)
      .build
  }
}
