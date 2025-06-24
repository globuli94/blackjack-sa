ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "s_aiService",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",
      "com.typesafe.akka" %% "akka-actor" % "2.8.8",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8",
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.3",
      "com.typesafe.play" %% "play-json" % "2.10.6",
      "com.lihaoyi" %% "requests" % "0.8.0",
    )
  )
