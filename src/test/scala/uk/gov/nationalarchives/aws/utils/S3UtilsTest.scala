package uk.gov.nationalarchives.aws.utils

import java.nio.file.Path
import java.util.concurrent.CompletableFuture

import cats.implicits.catsSyntaxOptionId
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, MockitoSugar}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{GetObjectRequest, GetObjectResponse, PutObjectRequest, PutObjectResponse}

class S3UtilsTest extends AnyFlatSpec with MockitoSugar with EitherValues {
  "The upload method" should "upload a file with the correct parameters" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val requestCaptor: ArgumentCaptor[PutObjectRequest] = ArgumentCaptor.forClass(classOf[PutObjectRequest])
    val pathCaptor: ArgumentCaptor[Path] = ArgumentCaptor.forClass(classOf[Path])
    when(s3AsyncClient.putObject(requestCaptor.capture(), pathCaptor.capture())).thenReturn(CompletableFuture.completedFuture(PutObjectResponse.builder().build()))
    val s3Utils = new S3Utils(s3AsyncClient)
    s3Utils.upload("bucket", "key", Path.of("path")).unsafeRunSync()
    val request: PutObjectRequest = requestCaptor.getValue

    request.bucket() should equal("bucket")
    request.key() should equal("key")
    pathCaptor.getValue.getFileName.toString should equal("path")
  }

  "The upload method" should "return an error if the upload fails" in {
    val s3AsyncClient = mock[S3AsyncClient]
    when(s3AsyncClient.putObject(any[PutObjectRequest], any[Path])).thenReturn(CompletableFuture.failedFuture(new RuntimeException("upload failed")))
    val s3Utils = new S3Utils(s3AsyncClient)
    val response = s3Utils.upload("bucket", "key", Path.of("path")).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("upload failed")
  }

  "The download method" should "download a file with the correct parameters" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val requestCaptor: ArgumentCaptor[GetObjectRequest] = ArgumentCaptor.forClass(classOf[GetObjectRequest])
    val pathCaptor: ArgumentCaptor[Path] = ArgumentCaptor.forClass(classOf[Path])
    when(s3AsyncClient.getObject(requestCaptor.capture(), pathCaptor.capture())).thenReturn(CompletableFuture.completedFuture(GetObjectResponse.builder().build()))
    val s3Utils = new S3Utils(s3AsyncClient)
    s3Utils.downloadFiles("bucket", "key", Path.of("path").some).unsafeRunSync()
    val request: GetObjectRequest = requestCaptor.getValue

    request.bucket() should equal("bucket")
    request.key() should equal("key")
    pathCaptor.getValue.getFileName.toString should equal("path")
  }

  "The download method" should "use the key for the download path if a download path is not provided" in {
    val s3AsyncClient = mock[S3AsyncClient]
    val pathCaptor: ArgumentCaptor[Path] = ArgumentCaptor.forClass(classOf[Path])
    when(s3AsyncClient.getObject(any[GetObjectRequest], pathCaptor.capture())).thenReturn(CompletableFuture.completedFuture(GetObjectResponse.builder().build()))
    val s3Utils = new S3Utils(s3AsyncClient)
    s3Utils.downloadFiles("bucket", "key", Option.empty).unsafeRunSync()
    pathCaptor.getValue.getFileName.toString should equal("key")
  }

  "The download method" should "return an error if the download fails" in {
    val s3AsyncClient = mock[S3AsyncClient]
    when(s3AsyncClient.getObject(any[GetObjectRequest], any[Path])).thenReturn(CompletableFuture.failedFuture(new RuntimeException("download failed")))
    val s3Utils = new S3Utils(s3AsyncClient)
    val response = s3Utils.downloadFiles("bucket", "key", Path.of("path").some).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("download failed")
  }
}
