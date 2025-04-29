ThisBuild / scalaVersion := "3.3.1"

lazy val tui = (project in file("m_tui"))
  .settings(
    name := "m_tui",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.8.0",        // HTTP client for making GET requests
      "io.circe" %% "circe-core" % "0.14.6",        // JSON encoding/decoding (for JSON parsing)
      "io.circe" %% "circe-generic" % "0.14.6",     // Automatic derivation of case classes
      "io.circe" %% "circe-parser" % "0.14.6"       // JSON parsing
    )
  )

lazy val s_tuiService = (project in file("."))
  .aggregate(tui)
  .settings(
    name := "s_tuiService",
    Compile / run := (tui / Compile / run).evaluated
  )