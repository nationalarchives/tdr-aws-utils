# TDR AWS Utils

This is a repository for useful methods and circe decoders. These can be used when working with AWS classes. Currently there are:

* S3 Event Decoder - The messages we get through  in a Lambda are an S3 message inside an SNS message inside an SQS message. This method returns all S3 events from the message along with a list of errors, if any.
* AWS Decoders - Circe decoders for working with the above classes which are used by the event decoder method. These are needed because circe won't auto decode case classes.
* SQS Utils - There is a send and a delete method for sqs messages.
* SES Utils - There is a send email method.
* ECR Utils - There are methods to list repositories, describe images and start an image scan.
* Step Function Utils - There is a send task success request method for the callback pattern

There are also clients for AWS services. These are configured with an http client, the region and an endpoint configurable in an aplication.conf file. There are:
* An SQS Client
* An S3 Client
* An SES Client
* An ECR client
* An SFN client (Step Function)

There may be other shared code we can put in here as time goes on.

## Publish local version

Run `sbt package publishLocal` to publish a local snapshot version that you can use when making changes to this library
and client code at the same time.
