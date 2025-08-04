package uk.gov.nationalarchives.aws.utils.ssm

import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.{GetParameterRequest, GetParameterResponse, Parameter}

class SSMUtilsSpec extends AnyFlatSpec with MockitoSugar {

  "getParameterValue" should "call ssm and return parameter value for given name" in {
    val expectedValue = "expectedValue"
    val client = mock[SsmClient]
    val parameterResponse = GetParameterResponse.builder()
      .parameter(Parameter.builder()
        .name("name")
        .value(expectedValue)
        .build())
      .build()

    when(client.getParameter(any[GetParameterRequest]())).thenReturn(parameterResponse)

    val response: String = SSMUtils(client).getParameterValue("name")
    response should be(expectedValue)
  }
}
