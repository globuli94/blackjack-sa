package controller.controllerComponent

import com.google.inject.Inject
import controller.ControllerInterface
import controller.util.{Event, Observable}
import model.{GameFactoryInterface, GameInterface}

import scala.util.{Failure, Try}

case class Controller @Inject (var game: GameInterface, gameFactory: GameFactoryInterface) extends ControllerInterface with Observable {

  override def getGame: GameInterface = game

  override def setGame(other: GameInterface): Unit = {
    game = other
    notifyObservers(Event.load)
  }

  override def initializeGame(): Unit = {
    game = game.initialize
    notifyObservers(Event.start)
  }

  override def startGame(): Try[Unit] = {
    game.startGame match {
      case Some(updatedGame: GameInterface) =>
        game = updatedGame
        notifyObservers(Event.start)
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
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def hitPlayer(): Try[Unit] = {
    game.hitPlayer match {
      case Some(updatedGame: GameInterface) =>
        game = updatedGame
        notifyObservers(Event.hitNextPlayer)
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
    sys.exit(0)
  }

  override def toString: String = {
    game.toString
  }
}
