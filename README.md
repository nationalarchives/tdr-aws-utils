## TDR AWS Utils

This is a repository for useful methods and circe decoders. These can be used when working with AWS classes. Currently there are:

* S3 Event Decoder - The messages we get through  in a Lambda are an S3 message inside an SNS message inside an SQS message. This method returns all S3 events from the message along with a list of errors, if any.
* AWS Decoders - Circe decoders for working with the above classes which are used by the event decoder method. These are needed because circe won't auto decode case classes.

There may be other shared code we can put in here as time goes on.