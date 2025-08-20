package uk.gov.nationalarchives.aws.utils.s3

import cats.effect.IO
import io.circe.Decoder
import io.circe.parser.decode
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model._
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest

import java.io.InputStream
import java.net.URL
import java.nio.file.{Path, Paths}
import java.time.Duration
import java.util.concurrent.CompletableFuture
import scala.annotation.tailrec
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters.CompletionStageOps


class S3Utils(client: S3AsyncClient, presigner: S3Presigner) {
  def toIO[T](fut: CompletableFuture[T]): IO[T] = IO.fromFuture(IO(fut.asScala))

  def upload(bucket: String, key: String, path: Path): IO[PutObjectResponse] = {
    toIO(client.putObject(PutObjectRequest.builder.bucket(bucket).key(key).build, path))
  }

  def downloadFiles(bucket: String, key: String, path: Option[Path] = None): IO[GetObjectResponse] = {
    toIO(client.getObject(GetObjectRequest.builder.bucket(bucket).key(key).build, path.getOrElse(Paths.get(key))))
  }

  private def getObjectBytes(request: GetObjectRequest): ResponseBytes[GetObjectResponse] = {
    client.getObject(request, AsyncResponseTransformer.toBytes[GetObjectResponse]).get()
  }

  /**
   * Method to get S3 object as Input Stream
   * @param bucket
   * Name of the bucket where the object is stored
   *
   * @param objectKey
   * Key of the object
   *
   * @return
   * Object as input stream
   * */
  def getObjectAsStream(bucket: String, objectKey: String): InputStream = {
    val request = GetObjectRequest.builder.bucket(bucket).key(objectKey).build()
    getObjectBytes(request).asInputStream()
  }

  /**
   * Method to decode a JSON object stored in S3
   * @param bucket
   * Name of the bucket where the object is stored
   *
   * @param jsonObjectKey
   * Key of the JSON object
   *
   * @param decoder
   * A circe decoder which will decode the JSON object to case class T
   *
   * @tparam T
   * Type of the output case class
   *
   * @return
   * JSON object decoded to case class of type T
   */
  def decodeS3JsonObject[T <: Product](bucket: String, jsonObjectKey: String)(implicit decoder: Decoder[T]): T = {
    val request: GetObjectRequest = GetObjectRequest.builder.bucket(bucket).key(jsonObjectKey).build()
    val jsonString = getObjectBytes(request).asByteArray().map(_.toChar).mkString
    decode[T](jsonString) match {
      case Left(error) => throw error
      case Right(value) => value
    }
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

  /** This is a wrapper method that returns a map of key-value tags from the specified S3 object .
   *
   * @param bucket
   * The bucket name containing the object.
   * @param key
   * Name of the object key
   * @return
   * A string-string map of all tags on the object.
   * @example
   *   - val bucketName = ConfigFactory.load().getString("s3.bucket")
   *   - val fileKey = "f63ee3c5-xxxx-4841-8963-875ee54dcd07"
   *   - val tags = s3utils.getObjectTags(bucketName, fileKey)
   * */
  def getObjectTags(bucket: String, file: String): Map[String, String] = {
    val getTaggingRequest = GetObjectTaggingRequest.builder
      .bucket(bucket)
      .key(file)
      .build

    client.getObjectTagging(getTaggingRequest).get.tagSet.asScala.map(tag => tag.key -> tag.value).toMap
  }

  /** This is a helper method that appends or updates one or more key-value tags to an S3 object in a specified bucket.
   *
   * @param bucket
   * The bucket name containing the object.
   * @param key
   * Name of the object key
   * @param tags
   * The object tags as a map of string key-value pairs.
   * @return
   * A response object that contains either a success or an error code.
   * @example
   *   - val bucketName = ConfigFactory.load().getString("s3.bucket")
   *   - val fileKey = "f63ee3c5-xxxx-4841-8963-875ee54dcd07"
   *   - val tags = Map("date_last_modified" -> "2023-10-01", "author" -> "John Doe")
   *   - val response = s3utils.addObjectTags(bucketName, fileKey, tags).unsafeRunSync()
   * */
  def addObjectTags(bucket: String, file: String, tags: Map[String, String]): IO[PutObjectTaggingResponse] = {
    val tagList = getObjectTags(bucket, file) ++ tags
    val updatedTags: Tagging = Tagging.builder
      .tagSet(tagList.map(toTag).toList.asJava).build

    putObjectTags(bucket, file, updatedTags)
  }

  private def putObjectTags(bucket: String, file: String, tags: Tagging): IO[PutObjectTaggingResponse] = {
    val putObjectTaggingRequest = PutObjectTaggingRequest.builder
      .bucket(bucket)
      .key(file)
      .tagging(tags)
      .build

    toIO(client.putObjectTagging(putObjectTaggingRequest))
  }

  private def toTag(t: (String, String)): Tag = Tag.builder.key(t._1).value(t._2).build

}

object S3Utils {
  val presigner: S3Presigner = S3Presigner.builder()
    .region(Region.EU_WEST_2)
    .build()

  def apply(client: S3AsyncClient, presigner: S3Presigner) = new S3Utils(client, presigner)
  def apply(client: S3AsyncClient): S3Utils = new S3Utils(client, presigner)
}
