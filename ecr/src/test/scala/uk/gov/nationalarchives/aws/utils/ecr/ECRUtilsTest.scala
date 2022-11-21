package uk.gov.nationalarchives.aws.utils.ecr

import cats.effect.unsafe.implicits.global
import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.services.ecr.EcrAsyncClient
import software.amazon.awssdk.services.ecr.model._
import uk.gov.nationalarchives.aws.utils.ECRUtils
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage

import java.util.concurrent.CompletableFuture

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

  "imageScanFindings" should "get images details from the AWS client" in {
    val repoName = "some-repository-name"
    val sha256Digest = "some-sha256-digest"
    val vulnerability1 = "CVE-2021-123456"
    val vulnerability2 = "CVE-2021-234567"

    val findings = DescribeImageScanFindingsResponse.builder
      .imageScanFindings(ImageScanFindings.builder()
        .findings(
          ImageScanFinding.builder().name(vulnerability1).build(),
          ImageScanFinding.builder().name(vulnerability2).build()
        )
        .build()
      )
      .build()
    val response = CompletableFuture.completedFuture(findings)
    val ecrClient = Mockito.mock(classOf[EcrAsyncClient])
    val ecrUtils = ECRUtils(ecrClient)

    val argumentCaptor: ArgumentCaptor[DescribeImageScanFindingsRequest] =
      ArgumentCaptor.forClass(classOf[DescribeImageScanFindingsRequest])

    doAnswer(() => response).when(ecrClient).describeImageScanFindings(argumentCaptor.capture())

    val scanFindings = ecrUtils.imageScanFindings(repoName, sha256Digest).unsafeRunSync()

    val actualRequest = argumentCaptor.getValue
    actualRequest.repositoryName should be(repoName)
    actualRequest.imageId.imageDigest should be(sha256Digest)

    scanFindings.imageScanFindings.findings.size should be(2)
    scanFindings.imageScanFindings.findings.get(0).name should be(vulnerability1)
    scanFindings.imageScanFindings.findings.get(1).name should be(vulnerability2)
  }
}
