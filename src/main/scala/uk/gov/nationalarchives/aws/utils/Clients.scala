package uk.gov.nationalarchives.aws.utils

import java.net.URI

import com.typesafe.config.{Config, ConfigFactory}
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.sqs.SqsClient

object Clients {

  val configFactory: Config = ConfigFactory.load

  def s3: S3Client = {
    val httpClient = ApacheHttpClient.builder.build
    S3Client.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(configFactory.getString("s3.endpoint")))
      .httpClient(httpClient)
      .build()
  }

  def sqs: SqsClient = {
    val httpClient = ApacheHttpClient.builder.build
    SqsClient.builder()
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(configFactory.getString("sqs.endpoint")))
      .httpClient(httpClient)
      .build()
  }

  def ses: SesClient = {
    val httpClient = ApacheHttpClient.builder.build
    SesClient.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(configFactory.getString("ses.endpoint")))
      .httpClient(httpClient)
      .build()
  }


}
