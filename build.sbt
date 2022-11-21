
import Dependencies._
import sbt.url
import sbtrelease.ReleaseStateTransformations._

ThisBuild / scalaVersion := "2.13.10"
ThisBuild / version := (ThisBuild / version).value
ThisBuild / organization := "uk.gov.nationalarchives.aws.utils"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/nationalarchives/tdr-aws-utils"),
    "git@github.com:nationalarchives/tdr-aws-utils"
  )
)
developers := List(
  Developer(
    id = "tna-digital-archiving-jenkins",
    name = "TNA Digital Archiving",
    email = "digitalpreservation@nationalarchives.gov.uk",
    url = url("https://github.com/nationalarchives/tdr-generated-grapqhl")
  )
)

ThisBuild / description := "A project containing useful methods and circe decoders for working with AWS classes"
ThisBuild / licenses := List("MIT" -> new URL("https://choosealicense.com/licenses/mit/"))
ThisBuild / homepage := Some(url("https://github.com/nationalarchives/tdr-aws-utils"))

useGpgPinentry := true
publishTo := sonatypePublishToBundle.value
publishMavenStyle := true

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

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    scalaTest % Test,
    mockito % Test,
    catsEffect
  )
)

lazy val s3 = (project in file("s3"))
  .settings(commonSettings).settings(
  name := "s3",
  libraryDependencies ++= Seq(
    s3Sdk
  )
)

lazy val ecr = (project in file("ecr"))
  .settings(commonSettings).settings(
  name := "ecr",
  libraryDependencies ++= Seq(
    ecrSdk
  )
)

lazy val sns = (project in file("sns"))
  .settings(commonSettings).settings(
  name := "sns",
  libraryDependencies ++= Seq(
    snsSdk
  )
)

lazy val sqs = (project in file("sqs"))
  .settings(commonSettings).settings(
  name := "sqs",
  libraryDependencies ++= Seq(
    sqsSdk
  )
)

lazy val stepFunction = (project in file("stepfunction"))
  .settings(commonSettings).settings(
  name := "stepfunction",
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
  libraryDependencies ++= Seq(
    sesSdk
  )
)

lazy val kms = (project in file("kms"))
  .settings(commonSettings).settings(
  name := "kms",
  libraryDependencies ++= Seq(
    kmsSdk
  )
)

lazy val decoders = (project in file("decoders"))
  .settings(commonSettings).settings(
  name := "decoders",
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
