import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1"
  lazy val mockito = "org.mockito" %% "mockito-scala" % "1.14.1"
  lazy val lambdaJavaCore = "com.amazonaws" % "aws-lambda-java-core" % "1.2.1"
  lazy val lambdaJavaEvents = "com.amazonaws" % "aws-lambda-java-events" % "3.1.0"
  lazy val s3Sdk = "software.amazon.awssdk" % "s3" % "2.13.18"
  lazy val sesSdk = "software.amazon.awssdk" % "ses" % "2.15.2"
  lazy val sqsSdk = "software.amazon.awssdk" % "sqs" % "2.13.15"
  lazy val ecrSdk = "software.amazon.awssdk" % "ecr" % "2.13.15"
  lazy val sfnSdk = "software.amazon.awssdk" % "sfn" % "2.15.79"
  lazy val typesafe = "com.typesafe" % "config" % "1.4.0"
  lazy val circeCore = "io.circe" %% "circe-core" % "0.13.0"
  lazy val circeGeneric = "io.circe" %% "circe-generic" % "0.13.0"
  lazy val circeParser = "io.circe" %% "circe-parser" % "0.13.0"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "2.2.0"
  lazy val monix = "io.monix" %% "monix" % "3.2.2"
  lazy val monixEval = "io.monix" %% "monix-eval" % "3.2.2"
}
