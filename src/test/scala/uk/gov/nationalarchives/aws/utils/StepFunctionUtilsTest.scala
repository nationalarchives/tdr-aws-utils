package uk.gov.nationalarchives.aws.utils

import java.util.concurrent.CompletableFuture

import io.circe.Json
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model.{SendTaskFailureRequest, SendTaskFailureResponse, SendTaskSuccessRequest, SendTaskSuccessResponse}
import uk.gov.nationalarchives.aws.utils.TestUtils.failedFuture

class StepFunctionUtilsTest extends AnyFlatSpec with MockitoSugar with EitherValues {

  private val taskToken: String = "taskToken"
  private val outputJson = Json.fromString("outputJson")
  private val causeMessage = "some error occurred"

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

  "The sendTaskSuccessRequest method" should "return an error if the request fails" in {
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

  "The sendTaskFailureRequest method" should "return an error if the request fails" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    when(stepFunctionClient.sendTaskFailure(any[SendTaskFailureRequest]))
      .thenReturn(failedFuture(new RuntimeException("Task failure request failed")))

    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)
    val response = stepFunctionUtils.sendTaskFailureRequest(taskToken, causeMessage).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("Task failure request failed")
  }
}
