import sbt._

object Dependencies {
  private val awsSdkVersion = "2.31.78"
  private val circeVersion = "0.14.14"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.19"
  lazy val mockito = "org.mockito" %% "mockito-scala" % "2.0.0"
  lazy val lambdaJavaCore = "com.amazonaws" % "aws-lambda-java-core" % "1.3.0"
  lazy val lambdaJavaEvents = "com.amazonaws" % "aws-lambda-java-events" % "3.16.0"
  lazy val s3Sdk = "software.amazon.awssdk" % "s3" % awsSdkVersion
  lazy val sesSdk = "software.amazon.awssdk" % "ses" % awsSdkVersion
  lazy val sqsSdk = "software.amazon.awssdk" % "sqs" % awsSdkVersion
  lazy val ecrSdk = "software.amazon.awssdk" % "ecr" % awsSdkVersion
  lazy val sfnSdk = "software.amazon.awssdk" % "sfn" % awsSdkVersion
  lazy val kmsSdk = "software.amazon.awssdk" % "kms" % awsSdkVersion
  lazy val snsSdk = "software.amazon.awssdk" % "sns" % awsSdkVersion
  lazy val secretsManagerSdk = "software.amazon.awssdk" % "secretsmanager" % awsSdkVersion
  lazy val typesafe = "com.typesafe" % "config" % "1.4.3"
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.6.2"
}
