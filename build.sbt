ThisBuild / scalaVersion := "3.3.1"

lazy val core = (project in file("m_model"))
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
  .dependsOn(core)
  .settings(
    name := "m_persistence",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
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
  .dependsOn(core)
  .dependsOn(persistence)
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

lazy val ai = (project in file("m_ai"))
  .dependsOn(core)
  .settings(
    name := "m_ai",
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

lazy val ui = (project in file("m_ui"))
  .dependsOn(core, controller)
  .settings(
    name := "m_ui",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8"
    ),
  )

lazy val app = (project in file("m_app"))
  .dependsOn(core, controller, persistence, ai, ui)
  .settings(
    name := "m_app",
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
    ),
  )

lazy val root = (project in file("."))
  .aggregate(core, persistence, controller, ai, ui, app)
  .settings(
    name := "blackjack",
    Compile / run := (app / Compile / run).evaluated
  )