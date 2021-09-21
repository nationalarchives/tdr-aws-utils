package uk.gov.nationalarchives.aws.utils

import java.nio.file.{Path, Paths}
import cats.effect.IO
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{GetObjectRequest, GetObjectResponse, PutObjectRequest, PutObjectResponse}
import uk.gov.nationalarchives.aws.utils.AWSDecoders.FutureUtils

class S3Utils(client: S3AsyncClient) {
  def upload(bucket: String, key: String, path: Path): IO[PutObjectResponse] = {
    client.putObject(PutObjectRequest.builder.bucket(bucket).key(key).build, path).toIO
  }

  def downloadFiles(bucket: String, key: String, path: Option[Path] = None): IO[GetObjectResponse] = {
    client.getObject(GetObjectRequest.builder.bucket(bucket).key(key).build, path.getOrElse(Paths.get(key))).toIO
  }
}

object S3Utils {
  def apply(client: S3AsyncClient): S3Utils = new S3Utils(client)
}
