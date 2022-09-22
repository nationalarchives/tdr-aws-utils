
import Dependencies._
import sbt.url
import sbtrelease.ReleaseStateTransformations._

ThisBuild / scalaVersion     := "2.13.9"
ThisBuild / version          := (ThisBuild / version ).value
ThisBuild / organization     := "uk.gov.nationalarchives"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/nationalarchives/tdr-aws-utils"),
    "git@github.com:nationalarchives/tdr-aws-utils"
  )
)
developers := List(
  Developer(
    id    = "tna-digital-archiving-jenkins",
    name  = "TNA Digital Archiving",
    email = "digitalpreservation@nationalarchives.gov.uk",
    url   = url("https://github.com/nationalarchives/tdr-generated-grapqhl")
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


lazy val root = (project in file("."))
  .settings(
    name := "tdr-aws-utils",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      mockito % Test,
      catsEffect,
      lambdaJavaCore,
      lambdaJavaEvents,
      ecrSdk,
      kmsSdk,
      sesSdk,
      s3Sdk,
      sqsSdk,
      typesafe,
      circeCore,
      circeGeneric,
      circeParser,
      sfnSdk,
      snsSdk
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
