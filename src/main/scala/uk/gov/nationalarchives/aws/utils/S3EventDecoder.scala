package uk.gov.nationalarchives.aws.utils

import com.amazonaws.services.lambda.runtime.events.{S3Event, SQSEvent}
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS
import io.circe
import scala.jdk.CollectionConverters._
import io.circe.parser.decode
import AWSDecoders._

object S3EventDecoder {

  def decodeS3EventFromSqs(event: SQSEvent): EventsWithErrors = {
    val eventsOrError: List[Either[circe.Error, EventWithReceiptHandle]] = event.getRecords.asScala.map(record => {
      for {
        snsDecoded <- decode[SNS](record.getBody)
        s3 <- decode[S3Event](snsDecoded.getMessage)
      } yield EventWithReceiptHandle(s3, record.getReceiptHandle)
    }).toList

    val (decodingFailed, decodingSucceeded) = eventsOrError.partitionMap(identity)
    EventsWithErrors(decodingSucceeded, decodingFailed)
  }
  case class EventWithReceiptHandle(event: S3Event, receiptHandle: String)
  case class EventsWithErrors(events: List[EventWithReceiptHandle], errors: List[circe.Error])


}
