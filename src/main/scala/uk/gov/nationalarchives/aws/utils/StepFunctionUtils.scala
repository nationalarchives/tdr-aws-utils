package uk.gov.nationalarchives.aws.utils

import cats.effect.IO
import io.circe.Json
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model.{SendTaskFailureRequest, SendTaskFailureResponse, SendTaskHeartbeatRequest, SendTaskHeartbeatResponse, SendTaskSuccessRequest, SendTaskSuccessResponse}
import uk.gov.nationalarchives.aws.utils.AWSDecoders.FutureUtils

class StepFunctionUtils(client: SfnAsyncClient) {

  def sendTaskSuccessRequest(taskToken: String, outputJson: Json): IO[SendTaskSuccessResponse] = {
    val request = SendTaskSuccessRequest.builder
      .taskToken(taskToken)
      .output(outputJson.toString())
      .build

    client.sendTaskSuccess(request).toIO
  }

  def sendTaskFailureRequest(taskToken: String, cause: String): IO[SendTaskFailureResponse] = {
    val request = SendTaskFailureRequest.builder
      .taskToken(taskToken)
      .cause(cause)
      .build

    client.sendTaskFailure(request).toIO
  }

  def sendTaskHeartbeat(taskToken: String): IO[SendTaskHeartbeatResponse] = {
    val request = SendTaskHeartbeatRequest.builder
      .taskToken(taskToken)
      .build()
    client.sendTaskHeartbeat(request).toIO
  }
}

object StepFunctionUtils {
  def apply(client: SfnAsyncClient): StepFunctionUtils = new StepFunctionUtils(client)
}
