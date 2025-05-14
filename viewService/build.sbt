ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "viewService",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "requests" % "0.8.3"
    )
  )