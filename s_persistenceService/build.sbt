ThisBuild / scalaVersion := "3.3.1"

lazy val validation = (project in file("m_validation"))
  .settings(
    name := "m_validation",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.8.0",
      "com.typesafe.play" %% "play-json" % "2.10.4"
    )
  )

lazy val persistence = (project in file("m_persistence"))
  .dependsOn(validation)
  .settings(
    name := "m_persistence",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.8.0",
      "com.typesafe.play" %% "play-json" % "2.10.4",
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8",
      "com.typesafe.slick" %% "slick" % "3.5.2",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.2",
      "org.slf4j" % "slf4j-api" % "1.7.36",
      "org.slf4j" % "slf4j-simple" % "1.7.36",
      "org.postgresql" % "postgresql" % "42.3.3",
    )
  )

lazy val root = (project in file("."))
  .aggregate(validation)
  .aggregate(persistence)
  .settings(
    name := "s_persistenceService",
    Compile / run := (persistence / Compile / run).evaluated
  )
