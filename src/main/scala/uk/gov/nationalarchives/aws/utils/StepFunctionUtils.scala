package uk.gov.nationalarchives.aws.utils

import cats.effect.IO
import io.circe.Json
import monix.catnap.syntax.SyntaxForLiftFuture
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model.{SendTaskSuccessRequest, SendTaskSuccessResponse}

class StepFunctionUtils(client: SfnAsyncClient) {

  def sendTaskSuccessRequest(taskToken: String, outputJson: Json): IO[SendTaskSuccessResponse] = {
    val request = SendTaskSuccessRequest.builder
      .taskToken(taskToken)
      .output(outputJson.toString())
      .build

    IO(client.sendTaskSuccess(request)).futureLift
  }
}

object StepFunctionUtils {
  def apply(client: SfnAsyncClient): StepFunctionUtils = new StepFunctionUtils(client)
}
