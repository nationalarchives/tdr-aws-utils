package uk.gov.nationalarchives.aws.utils.s3

import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.{S3AsyncClient, S3Client}

import java.net.URI

object S3Clients {
  def s3(endpoint: String): S3Client = {
    val httpClient = ApacheHttpClient.builder.build
    S3Client.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpoint))
      .httpClient(httpClient)
      .build()
  }

  def s3Async(endpoint: String, multipartEnabled: Boolean = true): S3AsyncClient = {
    val httpClient = NettyNioAsyncHttpClient.builder.build
    S3AsyncClient.builder
      .multipartEnabled(multipartEnabled)
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(endpoint))
      .httpClient(httpClient)
      .build
  }
}
