package controller.controllerComponent

import com.google.inject.Inject
import controller.ControllerInterface
import controller.util.{Event, Observable}
import fileIO.fileIOComponent.FileIOInterface
import model.modelComponent.{GameFactoryInterface, GameInterface}

import scala.util.{Failure, Try}

case class Controller @Inject (var game: GameInterface, gameFactory: GameFactoryInterface, fileIO: FileIOInterface) extends ControllerInterface with Observable {

  override def getGame: GameInterface = game

  override def setGame(other: GameInterface): Unit = {
    game = other
    notifyObservers(Event.load)
  }

  override def saveGame(): Unit = {
    fileIO.save(gameFactory, game)
    notifyObservers(Event.save)
  }

  override def loadGame(): Unit = {
    game = fileIO.load(gameFactory)
    notifyObservers(Event.load)
  }

  override def initializeGame(): Unit = {
    game = game.initialize
    notifyObservers(Event.start)
    saveGame()
  }

  override def startGame(): Try[Unit] = {
    game.startGame match {
      case Some(updatedGame: GameInterface) =>
        game = updatedGame
        notifyObservers(Event.start)
        saveGame()
        Try(())
      case _ =>
        notifyObservers(Event.invalidCommand)
        Failure(Exception("Game can't be started right now"))
    }
  }

  override def addPlayer(name: String): Try[Unit] = {
    game.createPlayer(name) match {
      case Some(updatedGame: GameInterface) =>
        game = updatedGame
        notifyObservers(Event.addPlayer)
        saveGame()
        Try(())
      case _ =>
        // Note: Event.errPlayerNameExists is currently not represented here anymore
        notifyObservers(Event.invalidCommand)
        Failure(new Exception("Cannot add players right now"))
    }
  }

  override def leavePlayer(): Unit = {
    if(game.getPlayers.nonEmpty) {
        game = game.leavePlayer()
        notifyObservers(Event.leavePlayer)
        saveGame()
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def hitPlayer(): Try[Unit] = {
    game.hitPlayer match {
      case Some(updatedGame: GameInterface) =>
        game = updatedGame
        notifyObservers(Event.hitNextPlayer)
        saveGame()
        Try(())
      case _ =>
        notifyObservers(Event.invalidCommand)
        Failure(Exception("Cannot hit player right now"))
    }
  }

  override def standPlayer(): Try[Unit] = {
    game.standPlayer match {
      case Some(updatedGame: GameInterface) =>
        game = updatedGame
        notifyObservers(Event.standNextPlayer)
        saveGame()
        Try(())
      case _ =>
        notifyObservers(Event.invalidCommand)
        Failure(Exception("Player can't stand right now"))
    }
  }

  override def doubleDown(): Try[Unit] = {
    game.doubleDownPlayer match {
      case Some(updatedGame: GameInterface) =>
        game = updatedGame
        notifyObservers(Event.doubleDown)
        saveGame()
        Try(())
      case _ =>
        notifyObservers(Event.invalidBet)
        Failure(Exception("Cannot double down right now"))
    }
  }

  override def bet(amount: String): Try[Unit] = {
    try {
      game.betPlayer(amount.toInt) match {
        case Some(updatedGame: GameInterface) =>
          game = updatedGame
          notifyObservers(Event.bet)
          saveGame()
          Try(())
        case _ =>
          notifyObservers(Event.invalidCommand)
          Failure(Exception("Invalid bet amount"))
      }
    } catch {
      case _: NumberFormatException =>
        notifyObservers(Event.invalidCommand)
        Failure(Exception("Bet was not integer value"))
    }
  }

  override def exit(): Unit = {
    saveGame()
    sys.exit(0)
  }

  override def toString: String = {
    game.toString
  }
}
