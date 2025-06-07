package uk.gov.nationalarchives.aws.utils.s3

import cats.effect.IO
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model._
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest

import java.net.URL
import java.nio.file.{Path, Paths}
import java.time.Duration
import java.util.concurrent.CompletableFuture
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.jdk.FutureConverters.CompletionStageOps

class S3Utils(client: S3AsyncClient, presigner: S3Presigner) {
  def toIO[T](fut: CompletableFuture[T]): IO[T] = IO.fromFuture(IO(fut.asScala))

  def upload(bucket: String, key: String, path: Path): IO[PutObjectResponse] = {
    toIO(client.putObject(PutObjectRequest.builder.bucket(bucket).key(key).build, path))
  }

  def downloadFiles(bucket: String, key: String, path: Option[Path] = None): IO[GetObjectResponse] = {
    toIO(client.getObject(GetObjectRequest.builder.bucket(bucket).key(key).build, path.getOrElse(Paths.get(key))))
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

  def listAllObjectsWithPrefix(bucket: String, objectPrefix: String, maxKeys: Int = 1000): List[S3Object] = {
    val request = ListObjectsV2Request.builder()
      .bucket(bucket)
      .prefix(objectPrefix)
      .maxKeys(Integer.valueOf(maxKeys))
      .build()

    @tailrec
    def innerFunction(response: ListObjectsV2Response, accumulator: List[S3Object]): List[S3Object] = {
      val nextToken = response.nextContinuationToken()
      if (nextToken == null) {
        response.contents().asScala.toList ::: accumulator
      } else {
        val nextRequest = ListObjectsV2Request.builder()
          .bucket(bucket)
          .prefix(objectPrefix)
          .maxKeys(Integer.valueOf(maxKeys))
          .continuationToken(nextToken)
          .build()
        val nextResponse = client.listObjectsV2(nextRequest).get()
        innerFunction(nextResponse, response.contents().asScala.toList ::: accumulator)
      }
    }
    innerFunction(client.listObjectsV2(request).get(), List())
  }
}

object S3Utils {
  val presigner: S3Presigner  = S3Presigner.builder()
    .region(Region.EU_WEST_2)
    .build()

  def apply(client: S3AsyncClient, presigner: S3Presigner) = new S3Utils(client, presigner)
  def apply(client: S3AsyncClient): S3Utils = new S3Utils(client, presigner)
}
