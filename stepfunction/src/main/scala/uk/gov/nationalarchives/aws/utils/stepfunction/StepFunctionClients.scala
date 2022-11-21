package uk.gov.nationalarchives.aws.utils.stepfunction

import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sfn.SfnAsyncClient

import java.net.URI

object StepFunctionClients {
  def sfnAsyncClient(endpointPath: String): SfnAsyncClient = {
    val httpClient = NettyNioAsyncHttpClient.builder.build
    SfnAsyncClient.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpointPath))
      .httpClient(httpClient)
      .build
  }
}
