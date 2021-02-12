
import Dependencies._
import sbt.url

ThisBuild / scalaVersion     := "2.13.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "uk.gov.nationalarchives.aws.utils"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/nationalarchives/tdr-aws-utils"),
    "git@github.com:nationalarchives/tdr-aws-utils"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "SP",
    name  = "Sam Palmer",
    email = "sam.palmer@nationalarchives.gov.uk",
    url   = url("http://tdr-transfer-integration.nationalarchives.gov.uk")
  )
)

ThisBuild / description := "A project containing useful methods and circe decoders for working with AWS classes"
ThisBuild / licenses := List("MIT" -> new URL("https://choosealicense.com/licenses/mit/"))
ThisBuild / homepage := Some(url("https://github.com/nationalarchives/tdr-aws utils"))

s3acl := None
s3sse := true
ThisBuild / publishMavenStyle := true

ThisBuild / publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(s3resolver.value(s"My ${prefix} S3 bucket", s3(s"tdr-$prefix-mgmt")))
}

lazy val root = (project in file("."))
  .settings(
    name := "tdr-aws-utils",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      mockito % Test,
      lambdaJavaCore,
      lambdaJavaEvents,
      ecrSdk,
      sesSdk,
      s3Sdk,
      sqsSdk,
      typesafe,
      circeCore,
      circeGeneric,
      circeParser,
      monix,
      monixEval,
      sfnSdk
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
