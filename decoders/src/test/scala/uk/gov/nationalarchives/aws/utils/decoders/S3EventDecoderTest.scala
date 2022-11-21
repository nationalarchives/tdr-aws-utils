package uk.gov.nationalarchives.aws.utils.decoders

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor3}
import uk.gov.nationalarchives.aws.utils.decoders.S3EventDecoder.EventsWithErrors

import scala.io.Source.fromResource
import scala.jdk.CollectionConverters._

class S3EventDecoderTest extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {
  val jsonOptions: TableFor3[String, String, Boolean] = Table(
    ("sns", "s3", "failed"),
    ("valid", "valid", false),
    ("invalid", "valid", true),
    ("valid", "invalid", true),
    ("invalid", "invalid", true),
  )

  val numberOfEvents: TableFor2[Int, Int] = Table(
    ("failed", "succeeded"),
    (1, 1),
    (1, 2),
    (2, 1),
    (3, 3)
  )

  def createEvent(locations: List[String]): SQSEvent = {
    val event = new SQSEvent()

    val records = locations.map(location => {
      val record = new SQSMessage()
      val body = fromResource(s"$location.json").mkString
      record.setBody(body)
      record
    })

    event.setRecords(records.asJava)
    event
  }

  forAll(jsonOptions) { (sns, s3, failed) => {
    "The decodeS3EventFromSqs method" should s"return a ${if (failed) "failure" else "success"} for $sns sns json and $s3 s3 json" in {
      val result: EventsWithErrors = S3EventDecoder.decodeS3EventFromSqs(createEvent(s"${sns}_sns_${s3}_s3" :: Nil))
      result.errors.nonEmpty should be(failed)
      result.events.isEmpty should be(failed)
    }
  }
  }

  forAll(numberOfEvents) { (failed, succeeded) => {
    "The decodeS3EventFromSqs method" should s"return $failed errors and $succeeded events" in {
      val inputs = List.fill(failed)("invalid_sns_invalid_s3") ++ List.fill(succeeded)("valid_sns_valid_s3")
      val result: EventsWithErrors = S3EventDecoder.decodeS3EventFromSqs(createEvent(inputs))
      result.errors.size should be(failed)
      result.events.size should be(succeeded)
    }
  }
  }

}
