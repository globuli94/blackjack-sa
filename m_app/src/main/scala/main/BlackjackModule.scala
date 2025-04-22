package main

import com.google.inject.AbstractModule
import controller.ControllerInterface
import controller.controllerComponent.Controller
import fileIO.fileIOComponent.JSON.FileIOJSON
import fileIO.fileIOComponent.XML.FileIOXML
import model.{GameFactoryInterface, GameInterface}
import controller.ControllerInterface
import model.modelComponent.{Game, GameFactory}
import net.codingwell.scalaguice.ScalaModule
import persistence.FileIOInterface

class BlackjackModule extends AbstractModule with ScalaModule {
  override def configure(): Unit =
    val game = Game()
    val gameFactory = GameFactory()
    bind[GameInterface].toInstance(game)
    bind[GameFactoryInterface].toInstance(gameFactory)
    bind[ControllerInterface].to[Controller].asEagerSingleton()

    val useJson = System.getProperty("fileio.json", "false").toBoolean
    if (useJson) {
      bind[FileIOInterface].to[FileIOJSON]
    } else {
      bind[FileIOInterface].to[FileIOXML]
    }
}