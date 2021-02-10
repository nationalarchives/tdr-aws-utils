package uk.gov.nationalarchives.aws.utils

import java.util.concurrent.CompletableFuture

import io.circe.Json
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, Mockito, MockitoSugar}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.services.sfn.SfnAsyncClient
import software.amazon.awssdk.services.sfn.model.{SendTaskSuccessRequest, SendTaskSuccessResponse}

class StepFunctionUtilsTest extends AnyFlatSpec with MockitoSugar with EitherValues {

  private val taskToken: String = "taskToken"
  private val outputJson = Json.fromString("outputJson")

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

  "The upload method" should "return an error if the upload fails" in {
    val stepFunctionClient = Mockito.mock(classOf[SfnAsyncClient])
    when(stepFunctionClient.sendTaskSuccess(any[SendTaskSuccessRequest]))
      .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Task success failed")))

    val stepFunctionUtils = StepFunctionUtils(stepFunctionClient)
    val response = stepFunctionUtils.sendTaskSuccessRequest(taskToken, outputJson).attempt.unsafeRunSync()
    response.left.value.getMessage should equal("Task success failed")
  }
}
