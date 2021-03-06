import sbt._

object Dependencies {
  private val awsSdkVersion = "2.15.79"
  private val circeVersion = "0.13.0"
  private val monixVersion = "3.2.2"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1"
  lazy val mockito = "org.mockito" %% "mockito-scala" % "1.14.1"
  lazy val lambdaJavaCore = "com.amazonaws" % "aws-lambda-java-core" % "1.2.1"
  lazy val lambdaJavaEvents = "com.amazonaws" % "aws-lambda-java-events" % "3.1.0"
  lazy val s3Sdk = "software.amazon.awssdk" % "s3" % awsSdkVersion
  lazy val sesSdk = "software.amazon.awssdk" % "ses" % awsSdkVersion
  lazy val sqsSdk = "software.amazon.awssdk" % "sqs" % awsSdkVersion
  lazy val ecrSdk = "software.amazon.awssdk" % "ecr" % awsSdkVersion
  lazy val sfnSdk = "software.amazon.awssdk" % "sfn" % awsSdkVersion
  lazy val kmsSdk = "software.amazon.awssdk" % "kms" % awsSdkVersion
  lazy val typesafe = "com.typesafe" % "config" % "1.4.0"
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "2.2.0"
  lazy val monix = "io.monix" %% "monix" % monixVersion
  lazy val monixEval = "io.monix" %% "monix-eval" % monixVersion
}
