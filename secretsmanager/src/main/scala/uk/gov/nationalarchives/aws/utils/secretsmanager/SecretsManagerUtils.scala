package uk.gov.nationalarchives.aws.utils.secretsmanager

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

class SecretsManagerUtils(secretsManagerClient: SecretsManagerClient) {

  def getSecretValueString(secretArn: String) = {
    val getSecretValueRequest = GetSecretValueRequest.builder.secretId(secretArn).build
    secretsManagerClient.getSecretValue(getSecretValueRequest).secretString()
  }
}

object SecretsManagerUtils {
  def apply(secretsManagerClient: SecretsManagerClient): SecretsManagerUtils = 
    new SecretsManagerUtils(secretsManagerClient)
}
