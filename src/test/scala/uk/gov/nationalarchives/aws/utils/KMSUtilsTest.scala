package uk.gov.nationalarchives.aws.utils

import org.mockito.{ArgumentCaptor, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.{DecryptRequest, DecryptResponse}

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.Base64
import scala.jdk.CollectionConverters.MapHasAsScala

class KMSUtilsTest extends AnyFlatSpec with MockitoSugar {
  "the decryptValue method" should "call kms with the correct arguments with the default encryption context" in {
    val encryptedValue = "encryptedValue"
    val mockResponse = DecryptResponse.builder().plaintext(SdkBytes.fromString("plain text", Charset.defaultCharset())).build()
    val client = mock[KmsClient]
    val decryptRequestCaptor: ArgumentCaptor[DecryptRequest] = ArgumentCaptor.forClass(classOf[DecryptRequest])
    doAnswer(() => mockResponse).when(client).decrypt(decryptRequestCaptor.capture())
    val response: String = KMSUtils(client).decryptValue(encryptedValue)
    response should be("plain text")
    val expectedCiperText = SdkBytes.fromByteBuffer(ByteBuffer.wrap(Base64.getDecoder.decode(encryptedValue)))
    decryptRequestCaptor.getValue.ciphertextBlob() should be(expectedCiperText)
  }

  "the decryptValue method" should "call kms with the correct custom encryption context" in {
    val mockResponse = DecryptResponse.builder().plaintext(SdkBytes.fromString("plain text", Charset.defaultCharset())).build()
    val client = mock[KmsClient]
    val decryptRequestCaptor: ArgumentCaptor[DecryptRequest] = ArgumentCaptor.forClass(classOf[DecryptRequest])
    doAnswer(() => mockResponse).when(client).decrypt(decryptRequestCaptor.capture())
    new KMSUtils(client, Map("customKey" -> "customValue")).decryptValue("encryptedValue")
    decryptRequestCaptor.getValue.encryptionContext().asScala("customKey") should be("customValue")

  }
}
