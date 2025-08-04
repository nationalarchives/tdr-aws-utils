# TDR AWS Utils

This is a repository for useful methods and circe decoders. These are all published as separate packages under the `uk.gov.nationalarchives` package.

## s3-utils
### Methods
* `downloadFiles` Will download a file to S3 to a specified path or to a path matching the S3 key.
* `upload` Will upload the file from a specified path to the supplied bucket and key.
* `generateGetObjectSignedUrl` Will generate a presigned url.

### Client
* `s3` The synchronous `S3Client` which takes an endpoint as an argument.
* `s3Async` An async `S3AsyncClient` which takes an endpoint as an argument.

## decoders-utils
### Methods
* `decodeS3EventFromSqs` Uses custom circe decoders to decode the Java classes from the AWS SDK to Scala case classes.

### Decoders
These are the list of custom circe decoders for decoding the AWS Java classes.

## sqs-utils
### Methods
* `send` Will send a message to an SQS queue.
* `delete` Will delete a message from an SQS queue
* `makeMessageVisible` Will make a message visible again. This is useful for retries on lambda errors.

### Clients
* `sqs` A synchronous `SqsClient` which takes an endpoint as an argument.

## ses-utils
### Methods
* `sendEmail` Will send an email based on the parameters in the `Email` case class.

### Clients
* `ses` A synchronous `SesClient` client which takes an endpoint as an argument.

## ecr-utils
### Methods
* `startImageScan` Will start an image scan for a specified image.
* `imageScanFindings` Will get the findings of a scan.
* `describeImages` Will list the images for a given repository name.
* `listRepositories` Will list all repositories for an account.

### Clients
* `ecr` An async `EcrAsyncClient` client which takes an endpoint as an argument.

## sns-utils
### Methods
* `publish` Will send a message to the specified topic

### Clients
* `sns` A synchronous `SnsClient` client which takes an endpoint as an argument.

## stepfunction-utils
### Methods
* `sendTaskSuccessRequest` Will send a custom json task success request for the provided task token.
* `sendTaskFailureRequest` Will send a failure request for the provided task token with a failure cause.
* `sendTaskHeartbeat` Will send a heartbeat for step function steps which are using it.
* `startExecution` Will start a step function execution.

### Clients
* `sfnAsyncClient` An async `SfnAsyncClient` client which takes an endpoint as an argument.

## kms-utils
### Methods
`decryptValue` will decrypt the encrypted value provided. If the decryption fails, the original value is returned. 

### Clients
* `kms` A synchronous `KmsClient` client which takes an endpoint as an argument.

## ssm-utils
### Methods
`getParameterValue` will retrieve the value associated with the given parameter name.

### Clients
* `ssm` A synchronous `SsmClient` client which takes an endpoint as an argument.

## Publish local version

Run `sbt package publishLocal` to publish a local version of all of these libraries in the `~/.ivy2/local/uk.gov.nationalarchives/` directory.

## Publish to Maven central
The `deploy.yml` GitHub action calls `sbt release` which releases all the modules as a separate package. It does not release the root `tdr-aws-utils` package as this is now empty. 
