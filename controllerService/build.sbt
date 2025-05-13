ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "controllerService",
    resolvers += "GitHub Packages" at "https://maven.pkg.github.com/globuli94/blackjack-sa",
    libraryDependencies ++= Seq(
      "com.github.globuli94" %% "gamelibrary" % "1.0.0"
    )
  )
