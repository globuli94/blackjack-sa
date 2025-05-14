ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "com.github.globuli94"

publishTo := Some("GitHub Packages" at s"https://maven.pkg.github.com/globuli94/blackjack-sa")

lazy val root = (project in file("."))
  .settings(
    name := "gameLibrary",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.8.8",
      "com.typesafe.akka" %% "akka-http" % "10.5.3",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.8.8",
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "7.0.0",
      "com.typesafe.play" %% "play-json" % "2.10.5",
    )
  )