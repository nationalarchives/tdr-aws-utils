package uk.gov.nationalarchives.aws.utils

import cats.effect.IO
import software.amazon.awssdk.services.ecr.EcrAsyncClient
import software.amazon.awssdk.services.ecr.model._
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage

import java.util.concurrent.CompletableFuture
import scala.jdk.FutureConverters.CompletionStageOps

class ECRUtils(client: EcrAsyncClient) {
  def toIO[T](fut: CompletableFuture[T]): IO[T] = IO.fromFuture(IO(fut.asScala))

  def startImageScan(ecrImage: EcrImage): IO[StartImageScanResponse] = {
    val imageIdentifier = ImageIdentifier.builder.imageDigest(ecrImage.imageDigest).imageTag(ecrImage.imageTag).build
    val request = StartImageScanRequest.builder
      .imageId(imageIdentifier)
      .repositoryName(ecrImage.repositoryMame)
      .build
    toIO(client.startImageScan(request))
  }

  def imageScanFindings(repositoryName: String, imageDigest: String): IO[DescribeImageScanFindingsResponse] = {
    val request = DescribeImageScanFindingsRequest.builder()
      .repositoryName(repositoryName)
      .imageId(ImageIdentifier.builder().imageDigest(imageDigest).build())
      .build()

    toIO(client.describeImageScanFindings(request))
  }

  def describeImages(repositoryName: String): IO[DescribeImagesResponse] = {
    val request = DescribeImagesRequest.builder().repositoryName(repositoryName).build()
    toIO(client.describeImages(request))
  }

  def listRepositories(): IO[DescribeRepositoriesResponse] = {
    toIO(client.describeRepositories())
  }
}

object ECRUtils {
  case class EcrImage(imageDigest: String, imageTag: String, repositoryMame: String)
  def apply(client: EcrAsyncClient): ECRUtils = new ECRUtils(client)
}
