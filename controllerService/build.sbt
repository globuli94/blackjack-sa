ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "controllerService",
    resolvers += "GitHub Packages" at "https://maven.pkg.github.com/globuli94/blackjack-sa",
    libraryDependencies ++= Seq(
      "com.github.globuli94" %% "gamelibrary" % "1.0.0",

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
