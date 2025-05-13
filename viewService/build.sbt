ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "viewService",
    resolvers += "GitHub Packages" at "https://maven.pkg.github.com/globuli94/blackjack-sa",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.8.3",        // HTTP client for making GET requests
      "io.circe" %% "circe-core" % "0.14.7",        // JSON encoding/decoding (for JSON parsing)
      "io.circe" %% "circe-generic" % "0.14.7",     // Automatic derivation of case classes
      "io.circe" %% "circe-parser" % "0.14.10",
      "com.lihaoyi" %% "requests" % "0.8.3",
      "com.typesafe.play" %% "play-json" % "2.10.5",
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8",
      "com.typesafe.slick" %% "slick" % "3.5.2",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.2",
      "org.slf4j" % "slf4j-api" % "2.0.16",
      "org.slf4j" % "slf4j-simple" % "2.0.16",
      "org.postgresql" % "postgresql" % "42.7.4",
    )
  )