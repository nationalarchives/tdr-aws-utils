
import Dependencies._
import sbt.url
import sbtrelease.ReleaseStateTransformations._

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    scalaTest % Test,
    mockito % Test,
    catsEffect
  ),
  scalaVersion := "2.13.10",
  version := version.value,
  organization := "uk.gov.nationalarchives.aws.utils",

  scmInfo := Some(
    ScmInfo(
      url("https://github.com/nationalarchives/tdr-aws-utils"),
      "git@github.com:nationalarchives/tdr-aws-utils"
    )
  ),
  developers := List(
    Developer(
      id = "tna-digital-archiving-jenkins",
      name = "TNA Digital Archiving",
      email = "digitalpreservation@nationalarchives.gov.uk",
      url = url("https://github.com/nationalarchives/tdr-generated-grapqhl")
    )
  ),

  licenses := List("MIT" -> new URL("https://choosealicense.com/licenses/mit/")),
  homepage := Some(url("https://github.com/nationalarchives/tdr-aws-utils")),

  useGpgPinentry := true,
  publishTo := sonatypePublishToBundle.value,
  publishMavenStyle := true,

  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommand("publishSigned"),
    releaseStepCommand("sonatypeBundleRelease"),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

lazy val s3 = (project in file("s3"))
  .settings(commonSettings).settings(
  name := "s3",
  description := "A project containing useful methods for interacting with S3",
  libraryDependencies ++= Seq(
    s3Sdk
  )
)

lazy val ecr = (project in file("ecr"))
  .settings(commonSettings).settings(
  name := "ecr",
  description := "A project containing useful methods for interacting with ECR",
  libraryDependencies ++= Seq(
    ecrSdk
  )
)

lazy val sns = (project in file("sns"))
  .settings(commonSettings).settings(
  name := "sns",
  description := "A project containing useful methods for interacting with SNS",
  libraryDependencies ++= Seq(
    snsSdk
  )
)

lazy val sqs = (project in file("sqs"))
  .settings(commonSettings).settings(
  name := "sqs",
  description := "A project containing useful methods for interacting with SQS",
  libraryDependencies ++= Seq(
    sqsSdk
  )
)

lazy val stepFunction = (project in file("stepfunction"))
  .settings(commonSettings).settings(
  name := "stepfunction",
  description := "A project containing useful methods for interacting with Step Functions",
  libraryDependencies ++= Seq(
    sfnSdk,
    circeCore,
    circeGeneric,
    circeParser
  )
)

lazy val ses = (project in file("ses"))
  .settings(commonSettings).settings(
  name := "ses",
  description := "A project containing useful methods for interacting with SES",
  libraryDependencies ++= Seq(
    sesSdk
  )
)

lazy val kms = (project in file("kms"))
  .settings(commonSettings).settings(
  name := "kms",
  description := "A project containing useful methods for interacting with KMS",
  libraryDependencies ++= Seq(
    kmsSdk
  )
)

lazy val decoders = (project in file("decoders"))
  .settings(commonSettings).settings(
  name := "decoders",
  description := "A project containing circe decoders for decoding SNS and S3 messages",
  libraryDependencies ++= Seq(
    lambdaJavaCore,
    lambdaJavaEvents,
    circeCore,
    circeGeneric,
    circeParser
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "tdr-aws-utils",
    libraryDependencies ++= Seq(

    )
  ).aggregate(s3, kms, ecr, ses, sns, sqs, stepFunction, decoders)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
