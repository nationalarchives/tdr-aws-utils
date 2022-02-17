package uk.gov.nationalarchives.aws.utils

import java.net.URL
import java.nio.file.{Path, Paths}
import java.time.Duration

import cats.effect.IO
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{GetObjectRequest, GetObjectResponse, PutObjectRequest, PutObjectResponse}
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.{GetObjectPresignRequest, PresignedGetObjectRequest}
import uk.gov.nationalarchives.aws.utils.AWSDecoders.FutureUtils

class S3Utils(client: S3AsyncClient) {
  def upload(bucket: String, key: String, path: Path): IO[PutObjectResponse] = {
    client.putObject(PutObjectRequest.builder.bucket(bucket).key(key).build, path).toIO
  }

  def downloadFiles(bucket: String, key: String, path: Option[Path] = None): IO[GetObjectResponse] = {
    client.getObject(GetObjectRequest.builder.bucket(bucket).key(key).build, path.getOrElse(Paths.get(key))).toIO
  }

  def generateSignedUrl(bucketName: String, keyName: String, durationInSeconds: Long = 60): URL = {
    val presigner: S3Presigner  = S3Presigner.builder()
      .region(Region.EU_WEST_2)
      .build()

    val getObjectRequest: GetObjectRequest =
      GetObjectRequest.builder()
        .bucket(bucketName)
        .key(keyName)
        .build()

    val getObjectPresignRequest: GetObjectPresignRequest = GetObjectPresignRequest.builder()
      .signatureDuration(Duration.ofSeconds(durationInSeconds))
      .getObjectRequest(getObjectRequest)
      .build()

    val presignedGetObjectRequest: PresignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest)
    presignedGetObjectRequest.url()

  }
}


object S3Utils {
  def apply(client: S3AsyncClient): S3Utils = new S3Utils(client)
}
