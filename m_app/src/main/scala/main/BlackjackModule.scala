package main

import com.google.inject.AbstractModule
import controller.ControllerInterface
import controller.controllerComponent.Controller
import fileIO.FileIOInterface
import fileIO.fileIOComponent.JSON.FileIOJSON
import fileIO.fileIOComponent.XML.FileIOXML
import model.GameInterface
import model.modelComponent.Game
import net.codingwell.scalaguice.ScalaModule

class BlackjackModule extends AbstractModule with ScalaModule {
  override def configure(): Unit =
    val game = Game()
    bind[GameInterface].toInstance(game)
    bind[ControllerInterface].to[Controller].asEagerSingleton()

    val useJson = System.getProperty("fileio.json", "false").toBoolean
    if (useJson) {
      bind[FileIOInterface].to[FileIOJSON]
    } else {
      bind[FileIOInterface].to[FileIOXML]
    }
}