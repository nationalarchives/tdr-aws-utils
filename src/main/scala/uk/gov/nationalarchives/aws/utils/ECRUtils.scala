package uk.gov.nationalarchives.aws.utils

import cats.effect.IO
import software.amazon.awssdk.services.ecr.EcrAsyncClient
import software.amazon.awssdk.services.ecr.model._
import uk.gov.nationalarchives.aws.utils.AWSDecoders.FutureUtils
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage

class ECRUtils(client: EcrAsyncClient) {

  def startImageScan(ecrImage: EcrImage): IO[StartImageScanResponse] = {
    val imageIdentifier = ImageIdentifier.builder.imageDigest(ecrImage.imageDigest).imageTag(ecrImage.imageTag).build
    val request = StartImageScanRequest.builder
      .imageId(imageIdentifier)
      .repositoryName(ecrImage.repositoryMame)
      .build
    client.startImageScan(request).toIO
  }

  def imageScanFindings(repositoryName: String, imageDigest: String): IO[DescribeImageScanFindingsResponse] = {
    val request = DescribeImageScanFindingsRequest.builder()
      .repositoryName(repositoryName)
      .imageId(ImageIdentifier.builder().imageDigest(imageDigest).build())
      .build()

    client.describeImageScanFindings(request).toIO
  }

  def describeImages(repositoryName: String): IO[DescribeImagesResponse] = {
    val request = DescribeImagesRequest.builder().repositoryName(repositoryName).build()
    client.describeImages(request).toIO
  }

  def listRepositories(): IO[DescribeRepositoriesResponse] = {
    client.describeRepositories().toIO
  }
}

object ECRUtils {
  case class EcrImage(imageDigest: String, imageTag: String, repositoryMame: String)
  def apply(client: EcrAsyncClient): ECRUtils = new ECRUtils(client)
}
