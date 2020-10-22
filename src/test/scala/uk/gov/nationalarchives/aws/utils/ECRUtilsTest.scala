package uk.gov.nationalarchives.aws.utils

import java.util.concurrent.CompletableFuture

import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import software.amazon.awssdk.services.ecr.{EcrAsyncClient, EcrClient}
import software.amazon.awssdk.services.ecr.model.{DescribeImagesRequest, DescribeImagesResponse, DescribeRepositoriesResponse, StartImageScanRequest, StartImageScanResponse}
import org.scalatest.matchers.should.Matchers._
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage

class ECRUtilsTest extends AnyFlatSpec with MockitoSugar {

  "The startImageScan method" should "be called with the correct parameters" in {
    val mockResponse = CompletableFuture.completedFuture(StartImageScanResponse.builder.build)
    val ecrClient = Mockito.mock(classOf[EcrAsyncClient])
    val ecrUtils = ECRUtils(ecrClient)
    val argumentCaptor: ArgumentCaptor[StartImageScanRequest] = ArgumentCaptor.forClass(classOf[StartImageScanRequest])

    doAnswer(() => mockResponse).when(ecrClient).startImageScan(argumentCaptor.capture())

    ecrUtils.startImageScan(EcrImage("digest", "tag", "repository")).unsafeRunSync()

    argumentCaptor.getValue.imageId.imageDigest should be("digest")
    argumentCaptor.getValue.imageId.imageTag should be("tag")
    argumentCaptor.getValue.repositoryName should be("repository")
  }

  "The getImages method" should "be called with the correct parameters" in {
    val mockResponse = CompletableFuture.completedFuture(DescribeImagesResponse.builder.build)
    val ecrClient = Mockito.mock(classOf[EcrAsyncClient])
    val ecrUtils = ECRUtils(ecrClient)
    val argumentCaptor: ArgumentCaptor[DescribeImagesRequest] = ArgumentCaptor.forClass(classOf[DescribeImagesRequest])

    doAnswer(() => mockResponse).when(ecrClient).describeImages(argumentCaptor.capture())

    ecrUtils.describeImages("repository").unsafeRunSync()

    argumentCaptor.getValue.repositoryName() should be("repository")
  }

  "The listRepositories method" should "be called with the correct parameters" in {
    val mockResponse = CompletableFuture.completedFuture(DescribeRepositoriesResponse.builder.build)
    val ecrClient = Mockito.mock(classOf[EcrAsyncClient])
    val ecrUtils = ECRUtils(ecrClient)

    doAnswer(() => mockResponse).when(ecrClient).describeRepositories()

    ecrUtils.listRepositories().unsafeRunSync()

    verify(ecrClient).describeRepositories()

  }
}
