ThisBuild / scalaVersion := "3.3.1"

lazy val model = (project in file("m_model"))
  .settings(
    name := "m_model",
    libraryDependencies ++= Seq(
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8"
    )
  )

lazy val persistence = (project in file("m_persistence"))
  .dependsOn(model)
  .settings(
    name := "m_persistence",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "com.typesafe.play" %% "play-json" % "2.10.5",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8"
    )
  )

lazy val controller = (project in file("m_controller"))
  .dependsOn(model, persistence)
  .settings(
    name := "m_controller",
    libraryDependencies ++= Seq(
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8"
    )
  )

lazy val s_gameService = (project in file("."))
  .aggregate(model, controller, persistence)
  .settings(
    name := "s_gameService",
    mainClass:= Some("ControllerServer"),
    Compile / run := (controller / Compile / run).evaluated
  )