addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.13")
resolvers += Resolver.jcenterRepo
addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.19.0")
libraryDependencies ++= Seq("javax.xml.bind" % "jaxb-api" % "2.3.0","com.sun.xml.bind" % "jaxb-ri" % "2.3.0")

