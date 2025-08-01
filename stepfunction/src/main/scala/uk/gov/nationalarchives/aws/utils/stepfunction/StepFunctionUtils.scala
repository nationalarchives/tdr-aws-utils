package uk.gov.nationalarchives.aws.utils.stepfunction

import cats.effect.IO
import io.circe.syntax._
import io.circe.{Encoder, Json, Printer}
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model._

import java.util.concurrent.CompletableFuture
import scala.jdk.FutureConverters.CompletionStageOps

class StepFunctionUtils(client: SfnAsyncClient) {
  private def toIO[T](fut: CompletableFuture[T]): IO[T] = IO.fromFuture(IO(fut.asScala))

  /** @param stateMachineArn
   *   The arn of the state machine to start
   * @param input
   *   A case class. This will be deserialised to a json string and sent as input to the step function.
   * @param name
   *   An optional step function name. If this is omitted, AWS will generate a UUID for a name.
   * @param enc
   *   A circe encoder which will encode the case class to JSON
   * @tparam T
   *   The type of the input case class
   * @return
   *   The response from the startExecution call
   */
  def startExecution[T <: Product](stateMachineArn: String, input: T, name: Option[String] = None)(implicit enc: Encoder[T]): IO[StartExecutionResponse] = {
    val builder = StartExecutionRequest.builder()
    val inputString = input.asJson.printWith(Printer.noSpaces)

    val startExecutionRequest: StartExecutionRequest = name
      .map(builder.name)
      .getOrElse(builder)
      .stateMachineArn(stateMachineArn)
      .input(inputString)
      .build()

    toIO(client.startExecution(startExecutionRequest))
  }

  /** @param executionArn
   *   The arn of the state machine execution
   * @return
   *   The response from the describeExecution call
   */
  def describeExecution(executionArn: String): IO[DescribeExecutionResponse] = {
    val request = DescribeExecutionRequest.builder()
      .executionArn(executionArn)
      .build()

    toIO(client.describeExecution(request))
  }

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
