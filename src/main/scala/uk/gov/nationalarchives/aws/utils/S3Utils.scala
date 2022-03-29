package uk.gov.nationalarchives.aws.utils

import java.net.URL
import java.nio.file.{Path, Paths}
import java.time.Duration

import cats.effect.IO
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{GetObjectRequest, GetObjectResponse, PutObjectRequest, PutObjectResponse}
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import uk.gov.nationalarchives.aws.utils.AWSDecoders.FutureUtils

class S3Utils(client: S3AsyncClient, presigner: S3Presigner) {
  def upload(bucket: String, key: String, path: Path): IO[PutObjectResponse] = {
    client.putObject(PutObjectRequest.builder.bucket(bucket).key(key).build, path).toIO
  }

  def downloadFiles(bucket: String, key: String, path: Option[Path] = None): IO[GetObjectResponse] = {
    client.getObject(GetObjectRequest.builder.bucket(bucket).key(key).build, path.getOrElse(Paths.get(key))).toIO
  }

  def generateGetObjectSignedUrl(bucketName: String, keyName: String, durationInSeconds: Long = 60): URL = {
    val getObjectRequest: GetObjectRequest =
      GetObjectRequest.builder()
        .bucket(bucketName)
        .key(keyName)
        .build()

    val getObjectPresignRequest: GetObjectPresignRequest = GetObjectPresignRequest.builder()
      .signatureDuration(Duration.ofSeconds(durationInSeconds))
      .getObjectRequest(getObjectRequest)
      .build()

    presigner.presignGetObject(getObjectPresignRequest).url()
  }
}

object S3Utils {
  val presigner: S3Presigner  = S3Presigner.builder()
    .region(Region.EU_WEST_2)
    .build()

  def apply(client: S3AsyncClient, presigner: S3Presigner) = new S3Utils(client, presigner)
  def apply(client: S3AsyncClient): S3Utils = new S3Utils(client, presigner)
}
