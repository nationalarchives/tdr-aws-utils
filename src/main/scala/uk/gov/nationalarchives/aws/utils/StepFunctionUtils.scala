package uk.gov.nationalarchives.aws.utils

import cats.effect.IO
import io.circe.Json
import monix.catnap.syntax.SyntaxForLiftFuture
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model.{SendTaskFailureRequest, SendTaskFailureResponse, SendTaskHeartbeatRequest, SendTaskHeartbeatResponse, SendTaskSuccessRequest, SendTaskSuccessResponse}

class StepFunctionUtils(client: SfnAsyncClient) {

  def sendTaskSuccessRequest(taskToken: String, outputJson: Json): IO[SendTaskSuccessResponse] = {
    val request = SendTaskSuccessRequest.builder
      .taskToken(taskToken)
      .output(outputJson.toString())
      .build

    IO(client.sendTaskSuccess(request)).futureLift
  }

  def sendTaskFailureRequest(taskToken: String, cause: String): IO[SendTaskFailureResponse] = {
    val request = SendTaskFailureRequest.builder
      .taskToken(taskToken)
      .cause(cause)
      .build

    IO(client.sendTaskFailure(request)).futureLift
  }

  def sendTaskHeartbeat(taskToken: String): IO[SendTaskHeartbeatResponse] = {
    val request = SendTaskHeartbeatRequest.builder
      .taskToken(taskToken)
      .build()
    IO(client.sendTaskHeartbeat(request)).futureLift
  }
}

object StepFunctionUtils {
  def apply(client: SfnAsyncClient): StepFunctionUtils = new StepFunctionUtils(client)
}
