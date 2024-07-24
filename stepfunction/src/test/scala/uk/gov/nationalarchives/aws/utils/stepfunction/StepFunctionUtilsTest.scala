package uk.gov.nationalarchives.aws.utils.stepfunction

import cats.effect.unsafe.implicits.global
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Encoder, Json}
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model._

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.failedFuture

class StepFunctionUtilsTest extends AnyFlatSpec with MockitoSugar with EitherValues {

  private val taskToken: String = "taskToken"
  private val outputJson = Json.fromString("outputJson")
  private val causeMessage = "some error occurred"

  implicit val inputEncoder: Encoder[SomeInput] = deriveEncoder[SomeInput]
  case class SomeInput(id: String, input1: String, input2: Int)

  private val input: SomeInput = SomeInput("id1", "input1", 2)

  "the startExecution method" should "be called with the correct parameters" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)
    val argumentCaptor: ArgumentCaptor[StartExecutionRequest] = ArgumentCaptor.forClass(classOf[StartExecutionRequest])
    val response = StartExecutionResponse.builder().build()

    when(stepFunctionClient.startExecution(argumentCaptor.capture())).thenReturn(CompletableFuture.completedFuture(response))

    stepFunctionUtils.startExecution("stepFunctionArn", input, Some("requestName")).unsafeRunSync()

    val request: StartExecutionRequest = argumentCaptor.getValue
    request.stateMachineArn() should equal("stepFunctionArn")
    request.name() should equal("requestName")
    request.input() should equal("{\"id\":\"id1\",\"input1\":\"input1\",\"input2\":2}")
  }

  "the the startExecution method" should "return an error if the request fails" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    when(stepFunctionClient.startExecution(any[StartExecutionRequest]))
      .thenReturn(failedFuture(new RuntimeException("Start execution request failed")))

    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)
    val response = stepFunctionUtils.startExecution("stepFunctionArn", input).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("Start execution request failed")
  }

  "the sendTaskSuccessRequest method" should "be called with the correct parameters" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)

    val argumentCaptor: ArgumentCaptor[SendTaskSuccessRequest] = ArgumentCaptor.forClass(classOf[SendTaskSuccessRequest])
    val response = SendTaskSuccessResponse.builder.build

    when(stepFunctionClient.sendTaskSuccess(argumentCaptor.capture())).thenReturn(CompletableFuture.completedFuture(response))

    stepFunctionUtils.sendTaskSuccessRequest(taskToken, outputJson).unsafeRunSync()

    val request: SendTaskSuccessRequest = argumentCaptor.getValue
    request.taskToken should equal("taskToken")
    request.output should equal(outputJson.toString())
  }

  "the sendTaskSuccessRequest method" should "return an error if the request fails" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    when(stepFunctionClient.sendTaskSuccess(any[SendTaskSuccessRequest]))
      .thenReturn(failedFuture(new RuntimeException("Task success request failed")))

    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)
    val response = stepFunctionUtils.sendTaskSuccessRequest(taskToken, outputJson).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("Task success request failed")
  }

  "the sendTaskFailureRequest method" should "be called with the correct parameters" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)

    val argumentCaptor: ArgumentCaptor[SendTaskFailureRequest] = ArgumentCaptor.forClass(classOf[SendTaskFailureRequest])
    val response = SendTaskFailureResponse.builder.build

    when(stepFunctionClient.sendTaskFailure(argumentCaptor.capture())).thenReturn(CompletableFuture.completedFuture(response))

    stepFunctionUtils.sendTaskFailureRequest(taskToken, causeMessage).unsafeRunSync()

    val request: SendTaskFailureRequest = argumentCaptor.getValue
    request.taskToken should equal("taskToken")
    request.cause should equal(causeMessage)
  }

  "the sendTaskFailureRequest method" should "return an error if the request fails" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    when(stepFunctionClient.sendTaskFailure(any[SendTaskFailureRequest]))
      .thenReturn(failedFuture(new RuntimeException("Task failure request failed")))

    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)
    val response = stepFunctionUtils.sendTaskFailureRequest(taskToken, causeMessage).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("Task failure request failed")
  }

  "the sendTaskHeartbeat method" should "be called with the correct parameters" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)

    val argumentCaptor: ArgumentCaptor[SendTaskHeartbeatRequest] = ArgumentCaptor.forClass(classOf[SendTaskHeartbeatRequest])
    val response = SendTaskHeartbeatResponse.builder.build

    when(stepFunctionClient.sendTaskHeartbeat(argumentCaptor.capture())).thenReturn(CompletableFuture.completedFuture(response))

    stepFunctionUtils.sendTaskHeartbeat(taskToken).unsafeRunSync()

    val request: SendTaskHeartbeatRequest = argumentCaptor.getValue
    request.taskToken should equal("taskToken")
  }

  "the sendTaskHeartbeat method" should "return an error if the request fails" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    when(stepFunctionClient.sendTaskHeartbeat(any[SendTaskHeartbeatRequest]))
      .thenReturn(failedFuture(new RuntimeException("Task heartbeat request failed")))

    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)
    val response = stepFunctionUtils.sendTaskHeartbeat(taskToken).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("Task heartbeat request failed")
  }
}
