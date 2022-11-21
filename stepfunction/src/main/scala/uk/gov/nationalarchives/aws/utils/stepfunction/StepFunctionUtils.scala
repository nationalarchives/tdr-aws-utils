package uk.gov.nationalarchives.aws.utils.stepfunction

import cats.effect.IO
import io.circe.Json
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model.{SendTaskFailureRequest, SendTaskFailureResponse, SendTaskHeartbeatRequest, SendTaskHeartbeatResponse, SendTaskSuccessRequest, SendTaskSuccessResponse}

import java.util.concurrent.CompletableFuture
import scala.jdk.FutureConverters.CompletionStageOps

class StepFunctionUtils(client: SfnAsyncClient) {
  def toIO[T](fut: CompletableFuture[T]): IO[T] = IO.fromFuture(IO(fut.asScala))

  def sendTaskSuccessRequest(taskToken: String, outputJson: Json): IO[SendTaskSuccessResponse] = {
    val request = SendTaskSuccessRequest.builder
      .taskToken(taskToken)
      .output(outputJson.toString())
      .build

    toIO(client.sendTaskSuccess(request))
  }

  def sendTaskFailureRequest(taskToken: String, cause: String): IO[SendTaskFailureResponse] = {
    val request = SendTaskFailureRequest.builder
      .taskToken(taskToken)
      .cause(cause)
      .build

    toIO(client.sendTaskFailure(request))
  }

  def sendTaskHeartbeat(taskToken: String): IO[SendTaskHeartbeatResponse] = {
    val request = SendTaskHeartbeatRequest.builder
      .taskToken(taskToken)
      .build()
    toIO(client.sendTaskHeartbeat(request))
  }
}

object StepFunctionUtils {
  def apply(client: SfnAsyncClient): StepFunctionUtils = new StepFunctionUtils(client)
}
