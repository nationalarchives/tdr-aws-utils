package uk.gov.nationalarchives.aws.utils

import java.net.URI

import com.typesafe.config.ConfigFactory
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

object Clients {

  def s3: S3Client = {
    val httpClient = ApacheHttpClient.builder.build
    val configFactory = ConfigFactory.load
    S3Client.builder
      .region(Region.EU_WEST_2)
      .endpointOverride(URI.create(configFactory.getString("s3.endpoint")))
      .httpClient(httpClient)
      .build()
  }


}
