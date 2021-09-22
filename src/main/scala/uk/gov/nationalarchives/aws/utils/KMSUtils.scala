package uk.gov.nationalarchives.aws.utils

import com.typesafe.config.ConfigFactory
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DecryptRequest

import java.nio.ByteBuffer
import java.util.Base64
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

class KMSUtils(client: KmsClient, encryptionContext: Map[String, String]) {

  def decryptValuesFromConfig(configPaths: List[String]): Map[String, String] = {
    val configFactory = ConfigFactory.load
    configPaths.map(path => {
      val value = configFactory.getString(path)
      val decryptedString: String = decryptValue(value)
      (path, decryptedString)
    }).toMap
  }

  def decryptValue(value: String): String = {
      Try {
        val decodedValue = Base64.getDecoder.decode(value)
        val decryptRequest = DecryptRequest.builder()
          .ciphertextBlob(SdkBytes.fromByteBuffer(ByteBuffer.wrap(decodedValue)))
          .encryptionContext(encryptionContext.asJava)
          .build()
        client.decrypt(decryptRequest).plaintext().asUtf8String()
      } match {
        // Return the original value on error. This will allow us to deploy without breaking the lambdas. It can be removed once all variables are encrypted
      case Failure(_) => value
        case Success(value) => value
      }
  }
}

object KMSUtils {
  def apply(client: KmsClient, encryptionContext: Map[String, String] = Map()) = new KMSUtils(client, encryptionContext)
}
