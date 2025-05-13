ThisBuild / scalaVersion := "3.3.1"



lazy val s_gameService = (project in file("s_gameService"))
  .settings(
    name := "s_gameService",
    mainClass := Some("controller.controllerServer.ControllerServer") // specify the main class here
  )

lazy val viewService = (project in file("viewService"))
  .settings(
    name := "viewService",
    mainClass := Some("tui.TUIApp") // specify the main class here
  )

lazy val root = (project in file("."))
  .aggregate(s_gameService, viewService)
  .dependsOn(s_gameService, viewService)
  .settings(
    name := "blackjack",
    Compile / run := (s_gameService / Compile / run).evaluated // default run for root
  )