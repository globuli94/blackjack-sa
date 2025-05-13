ThisBuild / scalaVersion := "3.3.1"

lazy val tui = (project in file("viewService"))
  .settings(
    name := "viewService",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.8.3",        // HTTP client for making GET requests
      "io.circe" %% "circe-core" % "0.14.7",        // JSON encoding/decoding (for JSON parsing)
      "io.circe" %% "circe-generic" % "0.14.7",     // Automatic derivation of case classes
      "io.circe" %% "circe-parser" % "0.14.10"       // JSON parsing
    )
  )

lazy val viewService = (project in file("."))
  .aggregate(tui)
  .settings(
    name := "viewService",
    Compile / run := (tui / Compile / run).evaluated
  )