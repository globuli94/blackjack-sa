ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "persistenceService",
    resolvers += "GitHub Packages" at "https://maven.pkg.github.com/globuli94/blackjack-sa",
    libraryDependencies ++= Seq(
      "com.github.globuli94" %% "gamelibrary" % "1.0.0",
      "org.mongodb.scala" % "mongo-scala-driver_2.13" % "5.1.1",
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
