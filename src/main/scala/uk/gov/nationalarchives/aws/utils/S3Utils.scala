package uk.gov.nationalarchives.aws.utils

import java.nio.file.{Path, Paths}

import cats.effect.IO
import monix.catnap.syntax._
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{GetObjectRequest, PutObjectRequest, PutObjectResponse}

class S3Utils(client: S3AsyncClient) {
  def upload(bucket: String, key: String, path: Path): IO[PutObjectResponse] = {
    IO(client.putObject(PutObjectRequest.builder.bucket(bucket).key(key).build, path)).futureLift
  }

  def downloadFiles(bucket: String, key: String, path: Option[Path] = None) = {
    IO(client.getObject(GetObjectRequest.builder.bucket(bucket).key(key).build, path.getOrElse(Paths.get(key)))).futureLift
  }
}

object S3Utils {
  def apply(client: S3AsyncClient): S3Utils = new S3Utils(client)
}
