package uk.gov.nationalarchives.aws.utils

import software.amazon.awssdk.services.ecr.{EcrAsyncClient, EcrClient}
import software.amazon.awssdk.services.ecr.model.{BatchGetImageRequest, DescribeImagesRequest, DescribeImagesResponse, DescribeRepositoriesResponse, ImageIdentifier, StartImageScanRequest, StartImageScanResponse}
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage
import cats.effect.IO
import monix.catnap.syntax._

class ECRUtils(client: EcrAsyncClient) {

  def startImageScan(ecrImage: EcrImage): IO[StartImageScanResponse] = {
    val imageIdentifier = ImageIdentifier.builder.imageDigest(ecrImage.imageDigest).imageTag(ecrImage.imageTag).build
    val request = StartImageScanRequest.builder
      .imageId(imageIdentifier)
      .repositoryName(ecrImage.repositoryMame)
      .build
    IO(client.startImageScan(request)).futureLift
  }

  def describeImages(repositoryName: String): IO[DescribeImagesResponse] = {
    val request = DescribeImagesRequest.builder().repositoryName(repositoryName).build()

    IO(client.describeImages(request)).futureLift
  }

  def listRepositories(): IO[DescribeRepositoriesResponse] = {
    IO(client.describeRepositories()).futureLift
  }

}

object ECRUtils {
  case class EcrImage(imageDigest: String, imageTag: String, repositoryMame: String)

  def apply(client: EcrAsyncClient): ECRUtils = new ECRUtils(client)
}
