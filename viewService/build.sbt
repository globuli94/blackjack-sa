ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "viewService",
    resolvers += "GitHub Packages" at "https://maven.pkg.github.com/globuli94/blackjack-sa"
  )