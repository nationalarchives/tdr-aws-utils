package uk.gov.nationalarchives.aws.utils.secretsmanager

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.{GetSecretValueRequest, GetSecretValueResponse}

class SecretsManagerUtilsSpec extends AnyFlatSpec with Matchers {

  "SecretsManagerUtils" should "retrieve secret value as string" in {
    val mockClient = mock(classOf[SecretsManagerClient])
    val secretArn = "arn:aws:secretsmanager:region:account:secret:name"
    val expectedSecretValue = """{"username":"admin","password":"secret123"}"""

    val mockResponse = GetSecretValueResponse.builder()
      .secretString(expectedSecretValue)
      .build()

    when(mockClient.getSecretValue(any(classOf[GetSecretValueRequest])))
      .thenReturn(mockResponse)

    val secretsManagerUtils = SecretsManagerUtils(mockClient)
    val actualSecretValue = secretsManagerUtils.getSecretValueString(secretArn)
    actualSecretValue shouldBe expectedSecretValue
  }
  
}
