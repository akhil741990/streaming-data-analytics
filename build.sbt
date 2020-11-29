organization := "com.soul"

name := "spark-kafka"

version := "0.5.0"

description := "Plug-and-play implementation of an Apache Spark custom data source for kafka"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.12", "2.12.7")



libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0" % "provided"
libraryDependencies += "com.google.guava" % "guava" % "14.0.1" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.3.4" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.7" % "provided"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"

libraryDependencies ++= {
    val log4j2Version = "2.11.1"
    Seq(
        "org.apache.logging.log4j" % "log4j-api" % log4j2Version % "test",
        "org.apache.logging.log4j" % "log4j-core" % log4j2Version % "test",
        "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j2Version % "test"
    )
}
assemblyShadeRules in assembly := Seq(
  ShadeRule.rename("org.apache.kafka.**" -> "shadedKafkaConnForSpark.@1").inAll
)

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}

fork in Test := true
javaOptions in Test ++= Seq("-Djava.library.path=./lib/sqlite4java", "-Daws.dynamodb.endpoint=http://localhost:8000")

/**
  * Maven specific settings for publishing to Maven central.
  */
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
