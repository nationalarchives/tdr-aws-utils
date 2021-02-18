package uk.gov.nationalarchives.aws.utils

import cats.effect.IO
import io.circe.Json
import monix.catnap.syntax.SyntaxForLiftFuture
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model.{SendTaskFailureRequest, SendTaskFailureResponse, SendTaskSuccessRequest, SendTaskSuccessResponse}
import uk.gov.nationalarchives.aws.utils.StepFunctionUtils.defaultErrorMessage

class StepFunctionUtils(client: SfnAsyncClient) {

  def sendTaskSuccessRequest(taskToken: String, outputJson: Json): IO[SendTaskSuccessResponse] = {
    val request = SendTaskSuccessRequest.builder
      .taskToken(taskToken)
      .output(outputJson.toString())
      .build

    IO(client.sendTaskSuccess(request)).futureLift
  }

  def sendTaskFailureRequest(taskToken: String, error: Option[String]): IO[SendTaskFailureResponse] = {
    val request = SendTaskFailureRequest.builder
      .taskToken(taskToken)
      .error(error.getOrElse(defaultErrorMessage))
      .build

    IO(client.sendTaskFailure(request)).futureLift
  }
}

object StepFunctionUtils {
  private val defaultErrorMessage = "Unknown task failure"
  def apply(client: SfnAsyncClient): StepFunctionUtils = new StepFunctionUtils(client)
}
