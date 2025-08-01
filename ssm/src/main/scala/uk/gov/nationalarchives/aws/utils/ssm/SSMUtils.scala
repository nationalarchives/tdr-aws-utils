package uk.gov.nationalarchives.aws.utils.ssm

import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.GetParameterRequest

class SSMUtils(client: SsmClient) {

  def getParameterValue(name: String): String = {
    val request = GetParameterRequest.builder()
      .name(name)
      .withDecryption(true)
      .build()
    client.getParameter(request).parameter().value()
  }
}

object SSMUtils {
  def apply(client: SsmClient) = new SSMUtils(client)
}
