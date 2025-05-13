package controller.controllerServer

import com.google.inject.AbstractModule
import controller.ControllerInterface
import controller.controllerComponent.Controller
import model.{Game, GameFactory, GameFactoryInterface, GameInterface}
import net.codingwell.scalaguice.ScalaModule
import serializer.GameStateSerializer
import serializer.JSON.JSONSerializer
import serializer.XML.XMLSerializer
import akka.actor.ActorSystem


class ControllerModule extends AbstractModule with ScalaModule {
  override def configure(): Unit =
    val game = Game()
    val gameFactory = GameFactory()
    bind[GameInterface].toInstance(game)
    bind[GameFactoryInterface].toInstance(gameFactory)
    bind[ControllerInterface].to[Controller].asEagerSingleton()
    bind[ActorSystem].toInstance(ActorSystem("ControllerActorSystem"))

    val useJson = System.getProperty("fileio.json", "true").toBoolean
    if (useJson) {
      bind[GameStateSerializer].to[JSONSerializer]
    } else {
      bind[GameStateSerializer].to[XMLSerializer]
    }
}