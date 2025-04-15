ThisBuild / scalaVersion := "3.3.1"

lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
    )
  )

lazy val persistence = (project in file("persistence"))
  .dependsOn(core)
  .settings(
    name := "persistence",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
      "com.typesafe.play" %% "play-json" % "2.10.5",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
    )
  )

lazy val controller = (project in file("controller"))
  .dependsOn(core)
  .dependsOn(persistence)
  .settings(
    name := "controller",
    libraryDependencies ++= Seq(
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
    )
  )

lazy val ai = (project in file("ai"))
  .dependsOn(core)
  .settings(
    name := "ai",
    libraryDependencies ++= Seq(
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
    )
  )

lazy val ui = (project in file("ui"))
  .dependsOn(core, controller)
  .settings(
    name := "ui",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
    ),
  )

lazy val app = (project in file("app"))
  .dependsOn(core, controller, persistence, ai, ui)
  .settings(
    name := "app",
    libraryDependencies ++= Seq(
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalamock" %% "scalamock" % "6.0.0" % Test,
      "org.mockito" % "mockito-core" % "5.14.2" % Test,
    ),
  )

lazy val root = (project in file("."))
  .aggregate(core, persistence, controller, ai, ui, app)
  .settings(
    name := "blackjack",
    Compile / run := (app / Compile / run).evaluated
  )