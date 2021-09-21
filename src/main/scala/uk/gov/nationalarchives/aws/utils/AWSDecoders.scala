package uk.gov.nationalarchives.aws.utils

import cats.effect.IO
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.{S3Entity, S3EventNotificationRecord}
import io.circe.{Decoder, HCursor}

import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters.CompletionStageOps

object AWSDecoders {

  implicit val snsDecoder: Decoder[SNS] = (c: HCursor) => for {
    signingCertUrl <- c.downField("SigningCertURL").as[String]
    messageId <- c.downField("MessageId").as[String]
    message <- c.downField("Message").as[String]
    subject <- c.downField("Subject").as[String]
    unsubscribeUrl <- c.downField("UnsubscribeURL").as[String]
    t <- c.downField("Type").as[String]
    signatureVersion <- c.downField("SignatureVersion").as[String]
    signature <- c.downField("Signature").as[String]
    topicArn <- c.downField("TopicArn").as[String]
  } yield new SNS().withMessage(message)
    .withSigningCertUrl(signingCertUrl)
    .withMessageId(messageId)
    .withMessage(message)
    .withSubject(subject)
    .withUnsubscribeUrl(unsubscribeUrl)
    .withType(t)
    .withSignatureVersion(signatureVersion)
    .withSignature(signature)
    .withTopicArn(topicArn)

  implicit val s3EntityDecoder: Decoder[S3Entity] = (c: HCursor) => for {
    configurationId <- c.downField("configurationId").as[String]
    bucket <- c.downField("bucket").as[S3EventNotification.S3BucketEntity]
    obj <- c.downField("object").as[S3EventNotification.S3ObjectEntity]
    schemaVersion <- c.downField("s3SchemaVersion").as[String]
  } yield new S3Entity(configurationId, bucket, obj, schemaVersion)

  implicit val s3BucketEntityDecoder: Decoder[S3EventNotification.S3BucketEntity] = (c: HCursor) => for {
    name <- c.downField("name").as[String]
    arn <- c.downField("arn").as[String]
    ownerIdentity <- c.downField("ownerIdentity").as[S3EventNotification.UserIdentityEntity]
  } yield new S3EventNotification.S3BucketEntity(name, ownerIdentity, arn)

  implicit val s3ObjectEntityDecoder: Decoder[S3EventNotification.S3ObjectEntity] = (c: HCursor) => for {
    key <- c.downField("key").as[String]
    size <- c.downField("size").as[Long]
    eTag <- c.downField("eTag").as[String]
    versionId <- c.downField("versionId").as[String]
    sequencer <- c.downField("sequencer").as[String]
  } yield new S3EventNotification.S3ObjectEntity(key, size, eTag, versionId, sequencer)

  implicit val requestParametersEntityDecoder: Decoder[S3EventNotification.RequestParametersEntity] = (c: HCursor) => for {
    sourceIPAddress <- c.downField("sourceIPAddress").as[String]
  } yield new S3EventNotification.RequestParametersEntity(sourceIPAddress)

  implicit val responseElementsEntityDecoder: Decoder[S3EventNotification.ResponseElementsEntity] = (c: HCursor) => for {
    xAmzRequestId <- c.downField("x-amz-request-id").as[String]
    xAmzId <- c.downField("x-amz-id-2").as[String]
  } yield new S3EventNotification.ResponseElementsEntity(xAmzId, xAmzRequestId)

  implicit val userIdentityEntityDecoder: Decoder[S3EventNotification.UserIdentityEntity] = (c: HCursor) => for {
    principalId <- c.downField("principalId").as[String]
  } yield new S3EventNotification.UserIdentityEntity(principalId)

  implicit val s3EventNotificationRecordDecoder: Decoder[S3EventNotificationRecord] = (c: HCursor) => for {
    awsRegion <- c.downField("awsRegion").as[String]
    eventName <- c.downField("eventName").as[String]
    eventSource <- c.downField("eventSource").as[String]
    eventTime <- c.downField("eventTime").as[String]
    eventVersion <- c.downField("eventVersion").as[String]
    s3 <- c.downField("s3").as[S3Entity]
    userIdentity <- c.downField("userIdentity").as[S3EventNotification.UserIdentityEntity]
    responseElements <- c.downField("responseElements").as[S3EventNotification.ResponseElementsEntity]
    requestParameters <- c.downField("requestParameters").as[S3EventNotification.RequestParametersEntity]
  } yield new S3EventNotificationRecord(awsRegion, eventName, eventSource, eventTime, eventVersion, requestParameters, responseElements, s3, userIdentity)

  implicit val s3Decoder: Decoder[S3Event] = (c: HCursor) => for {
    records <- c.downField("Records").as[List[S3EventNotificationRecord]]
  } yield {
    new S3Event(records.asJava)
  }

  implicit class FutureUtils[T](f: CompletableFuture[T]) {
    def toIO: IO[T] = IO.fromFuture(IO(f.asScala))
  }
}
