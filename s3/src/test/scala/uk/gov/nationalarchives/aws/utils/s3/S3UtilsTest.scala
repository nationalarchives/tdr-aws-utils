package uk.gov.nationalarchives.aws.utils.s3

import cats.effect.unsafe.implicits.global
import cats.implicits._
import io.circe.{Decoder, DecodingFailure, ParsingFailure}
import io.circe.generic.semiauto.deriveDecoder
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, MockitoSugar}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model._
import software.amazon.awssdk.services.s3.presigner.S3Presigner

import java.nio.file.{Path, Paths}
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.failedFuture
import scala.concurrent.ExecutionException
import scala.jdk.CollectionConverters._

class S3UtilsTest extends AnyFlatSpec with MockitoSugar with EitherValues {
  case class CCaseClass(propertyC: String)
  case class ACaseClass(propertyA: String, propertyB: Int, propertyC: CCaseClass)

  implicit val cDecoder: Decoder[CCaseClass] = deriveDecoder[CCaseClass]
  implicit val aDecoder: Decoder[ACaseClass] = deriveDecoder[ACaseClass]

  "decodeS3JsonObject" should "decode a valid JSON object from S3" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val mockResponseBytes = mock[ResponseBytes[GetObjectResponse]]
    val mockCompletableFuture = CompletableFuture.completedFuture(mockResponseBytes)
    when(mockResponseBytes.asByteArray()).thenReturn("{\"propertyA\":\"property_a\",\"propertyB\":2,\"propertyC\":{\"propertyC\":\"property_c\"}}".getBytes)

    val s3Utils = S3Utils(s3AsyncClient)
    when(s3AsyncClient.getObject(any[GetObjectRequest], any[AsyncResponseTransformer[GetObjectResponse, ResponseBytes[GetObjectResponse]]]))
      .thenReturn(mockCompletableFuture)

    val result = s3Utils.decodeS3JsonObject("bucket-name", "json/object/key")(aDecoder)
    result.propertyA should equal("property_a")
    result.propertyB should equal(2)
    result.propertyC.propertyC should equal("property_c")
  }

  "decodeS3JsonObject" should "throw an error when object from S3 is not JSON" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val mockResponseBytes = mock[ResponseBytes[GetObjectResponse]]
    val mockCompletableFuture = CompletableFuture.completedFuture(mockResponseBytes)
    when(mockResponseBytes.asByteArray()).thenReturn("a string".getBytes)

    val s3Utils = S3Utils(s3AsyncClient)
    when(s3AsyncClient.getObject(any[GetObjectRequest], any[AsyncResponseTransformer[GetObjectResponse, ResponseBytes[GetObjectResponse]]]))
      .thenReturn(mockCompletableFuture)

    val exception = intercept[ParsingFailure] {
      s3Utils.decodeS3JsonObject("bucket-name", "json/object/key")(aDecoder)
    }

    exception.getMessage() should equal("expected json value got 'a stri...' (line 1, column 1)")
  }

  "decodeS3JsonObject" should "throw an error when the JSON object is not of the correct type" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val mockResponseBytes = mock[ResponseBytes[GetObjectResponse]]
    val mockCompletableFuture = CompletableFuture.completedFuture(mockResponseBytes)
    when(mockResponseBytes.asByteArray()).thenReturn("{\"propertyD\":\"property_d\"}".getBytes)

    val s3Utils = S3Utils(s3AsyncClient)
    when(s3AsyncClient.getObject(any[GetObjectRequest], any[AsyncResponseTransformer[GetObjectResponse, ResponseBytes[GetObjectResponse]]]))
      .thenReturn(mockCompletableFuture)

    val exception = intercept[DecodingFailure] {
      s3Utils.decodeS3JsonObject("bucket-name", "json/object/key")(aDecoder)
    }

    exception.getMessage() should equal("DecodingFailure at .propertyA: Missing required field")
  }

  "The upload method" should "upload a file with the correct parameters" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val requestCaptor: ArgumentCaptor[PutObjectRequest] = ArgumentCaptor.forClass(classOf[PutObjectRequest])
    val pathCaptor: ArgumentCaptor[Path] = ArgumentCaptor.forClass(classOf[Path])
    when(s3AsyncClient.putObject(requestCaptor.capture(), pathCaptor.capture())).thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()))
    val s3Utils = S3Utils(s3AsyncClient)
    s3Utils.upload("bucket", "key", Paths.get("path")).unsafeRunSync()
    val request: PutObjectRequest = requestCaptor.getValue

    request.bucket() should equal("bucket")
    request.key() should equal("key")
    pathCaptor.getValue.getFileName.toString should equal("path")
  }

  "The upload method" should "return an error if the upload fails" in {
    val s3AsyncClient = mock[S3AsyncClient]
    when(s3AsyncClient.putObject(any[PutObjectRequest], any[Path])).thenReturn(failedFuture(new RuntimeException("upload failed")))
    val s3Utils = S3Utils(s3AsyncClient)
    val response = s3Utils.upload("bucket", "key", Paths.get("path")).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("upload failed")
  }

  "The download method" should "download a file with the correct parameters" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val requestCaptor: ArgumentCaptor[GetObjectRequest] = ArgumentCaptor.forClass(classOf[GetObjectRequest])
    val pathCaptor: ArgumentCaptor[Path] = ArgumentCaptor.forClass(classOf[Path])
    when(s3AsyncClient.getObject(requestCaptor.capture(), pathCaptor.capture())).thenReturn(CompletableFuture.completedFuture(GetObjectResponse.builder().build()))
    val s3Utils = S3Utils(s3AsyncClient)
    s3Utils.downloadFiles("bucket", "key", Paths.get("path").some).unsafeRunSync()
    val request: GetObjectRequest = requestCaptor.getValue

    request.bucket() should equal("bucket")
    request.key() should equal("key")
    pathCaptor.getValue.getFileName.toString should equal("path")
  }

  "The download method" should "use the key for the download path if a download path is not provided" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val pathCaptor: ArgumentCaptor[Path] = ArgumentCaptor.forClass(classOf[Path])
    when(s3AsyncClient.getObject(any[GetObjectRequest], pathCaptor.capture())).thenReturn(CompletableFuture.completedFuture(GetObjectResponse.builder().build()))
    val s3Utils = S3Utils(s3AsyncClient)
    s3Utils.downloadFiles("bucket", "key", Option.empty).unsafeRunSync()
    pathCaptor.getValue.getFileName.toString should equal("key")
  }

  "The download method" should "return an error if the download fails" in {
    val s3AsyncClient = mock[S3AsyncClient]
    when(s3AsyncClient.getObject(any[GetObjectRequest], any[Path])).thenReturn(failedFuture(new RuntimeException("download failed")))
    val s3Utils = S3Utils(s3AsyncClient)
    val response = s3Utils.downloadFiles("bucket", "key", Paths.get("path").some).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("download failed")
  }

  "The generateGetObjectSignedUrl" should "create a valid 'get object' pre-signed url" in {
    val s3AsyncClient = mock[S3AsyncClient]

    val presigner: S3Presigner = S3Presigner.builder()
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("A", "B")))
      .region(Region.EU_WEST_2)
      .build()
      .asInstanceOf[S3Presigner]

    val s3Utils = new S3Utils(s3AsyncClient, presigner)
    val url = s3Utils.generateGetObjectSignedUrl("some-bucket-name", "some-bucket-object")
    url.getHost shouldBe "some-bucket-name.s3.eu-west-2.amazonaws.com"
    url.getFile.contains("some-bucket-object") shouldBe true
    url.getAuthority shouldBe "some-bucket-name.s3.eu-west-2.amazonaws.com"
    url.getProtocol shouldBe "https"
  }

  "'listAllObjectsWithPrefix' method" should "return all the objects from the responses" in {
    val s3Object1 = S3Object.builder().build()
    val s3Object2 = S3Object.builder().build()
    val s3Object3 = S3Object.builder().build()
    val firstList = List(s3Object1, s3Object2).asJavaCollection
    val secondList = List(s3Object3).asJavaCollection
    val firstResponse = ListObjectsV2Response.builder()
      .nextContinuationToken("token2")
      .contents(firstList)
      .build()
    val secondResponse = ListObjectsV2Response.builder()
      .contents(secondList)
      .build()
    val s3AsyncClient = mock[S3AsyncClient]
    when(s3AsyncClient.listObjectsV2(any[ListObjectsV2Request]))
      .thenReturn(CompletableFuture.completedFuture(firstResponse), CompletableFuture.completedFuture(secondResponse))

    val s3Utils = S3Utils(s3AsyncClient)
    val response = s3Utils.listAllObjectsWithPrefix("some-bucket", "some/prefix")
    response.size should equal(3)
    response.contains(s3Object1) should equal(true)
    response.contains(s3Object2) should equal(true)
    response.contains(s3Object3) should equal(true)
  }

  "'listAllObjectsWithPrefix' method" should "return an error if the request failed" in {
    val s3AsyncClient = mock[S3AsyncClient]
    when(s3AsyncClient.listObjectsV2(any[ListObjectsV2Request])).thenReturn(failedFuture(new RuntimeException("Request failed")))
    val s3Utils = S3Utils(s3AsyncClient)
    val exception = intercept[ExecutionException] {
      s3Utils.listAllObjectsWithPrefix("some-bucket", "some/prefix")
    }
    exception.getMessage should equal("java.lang.RuntimeException: Request failed")
  }

  "'addObjectTags' method" should "correctly add tags to an object" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val getTagsResponse: ArgumentCaptor[GetObjectTaggingRequest] = ArgumentCaptor.forClass(classOf[GetObjectTaggingRequest])
    val putTagsResponse: ArgumentCaptor[PutObjectTaggingRequest] = ArgumentCaptor.forClass(classOf[PutObjectTaggingRequest])
    when(s3AsyncClient.getObjectTagging(getTagsResponse.capture())).thenReturn(CompletableFuture.completedFuture(GetObjectTaggingResponse.builder().build()))
    when(s3AsyncClient.putObjectTagging(putTagsResponse.capture())).thenReturn(CompletableFuture.completedFuture(PutObjectTaggingResponse.builder().build()))
    val s3Utils = S3Utils(s3AsyncClient)
    val tags = Map("key1" -> "value1", "key2" -> "value2")
    s3Utils.addObjectTags("bucket", "key", tags).attempt.unsafeRunSync()
    val request: PutObjectTaggingRequest = putTagsResponse.getValue

    request.bucket() should equal("bucket")
    request.key() should equal("key")
    request.tagging().tagSet().asScala.map(tag => (tag.key(), tag.value())).toMap should equal(tags)
  }

  "'addObjectTags' method" should "return an error if adding tags fails" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val getTagsResponse: ArgumentCaptor[GetObjectTaggingRequest] = ArgumentCaptor.forClass(classOf[GetObjectTaggingRequest])
    when(s3AsyncClient.getObjectTagging(getTagsResponse.capture())).thenReturn(CompletableFuture.completedFuture(GetObjectTaggingResponse.builder().build()))
    when(s3AsyncClient.putObjectTagging(any[PutObjectTaggingRequest])).thenReturn(failedFuture(new RuntimeException("Add tags request failed")))
    val s3Utils = S3Utils(s3AsyncClient)
    val response = s3Utils.addObjectTags("bucket", "key", Map("key1" -> "value1")).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("Add tags request failed")
  }
}
