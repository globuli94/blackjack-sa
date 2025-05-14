ThisBuild / scalaVersion := "3.3.1"

lazy val gameLibrary = project in file("gameLibrary")

lazy val persistenceService = (project in file("persistenceService"))
  .settings(
    name := "persistenceService",
    mainClass := Some("persistenceServer.PersistenceServer")
  )

lazy val controllerService = (project in file("controllerService"))
  .settings(
    name := "controllerService",
    mainClass := Some("controller.controllerServer.ControllerServer") // specify the main class here
  )

lazy val viewService = (project in file("viewService"))
  .settings(
    name := "viewService",
    mainClass := Some("tui.TUIApp") // specify the main class here
  )