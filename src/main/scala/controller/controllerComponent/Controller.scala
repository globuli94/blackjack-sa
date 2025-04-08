package controller.controllerComponent

import com.google.inject.Inject
import controller.ControllerInterface
import model.GameInterface
import model.modelComponent.GameState
import util.Event.*
import util.{Event, FileIOInterface, Observable}

import scala.util.{Failure, Try}

case class Controller @Inject (var game: GameInterface, fileIO: FileIOInterface) extends ControllerInterface with Observable {

  override def getGame: GameInterface = game

  override def saveGame(): Unit = {
    fileIO.save(game)
    notifyObservers(Event.save)
  }

  override def loadGame(): Unit = {
    game = fileIO.load()
    notifyObservers(Event.load)
  }

  override def initializeGame(): Unit = {
    game = game.initialize
    notifyObservers(Event.start)
    saveGame()
  }

  override def startGame(): Try[Unit] = {
    game.startGame match {
      case Some(updatedGame) =>
        game = updatedGame
        notifyObservers(Event.start)
        saveGame()
        Try(())
      case None =>
        notifyObservers(Event.invalidCommand)
        Failure(Exception("Game can't be started right now"))
    }
  }

  override def addPlayer(name: String): Try[Unit] = {
    game.createPlayer match {
      case Some(updatedGame) =>
        game = updatedGame
        notifyObservers(Event.addPlayer)
        saveGame()
        Try(())
      case None =>
        // Note: Event.errPlayerNameExists is currently not represented here anymore
        notifyObservers(Event.invalidCommand)
        Failure(Exception("Cannot add players right now"))
    }
  }

  override def leavePlayer(): Unit = {
    if(game.getPlayers.nonEmpty) {
        game = game.leavePlayer()
        notifyObservers(Event.leavePlayer)
        saveGame()
    } else {
      notifyObservers(invalidCommand)
    }
    
    
  }

  override def hitPlayer(): Unit = {
    val player = game.getPlayers(game.getIndex)
    if (player.hand.canHit && game.getState == GameState.Started) {
      game = game.hitPlayer
      notifyObservers(Event.hitNextPlayer)
      saveGame()
    } else {
      notifyObservers(Event.invalidCommand)
    }
  }

  override def standPlayer(): Unit = {
    if (game.getState == GameState.Started) {
      game = game.standPlayer
      notifyObservers(Event.standNextPlayer)
      saveGame()
    } else {
      notifyObservers(Event.invalidCommand)
    }

  }

  override def doubleDown(): Unit = {
    val player = game.getPlayers(game.getIndex)

    if (game.getState == GameState.Started && player.hand.canDoubleDown && player.bet <= player.money) {
      game = game.doubleDownPlayer
      notifyObservers(Event.doubleDown)
      saveGame()
    } else {
      notifyObservers(Event.invalidBet)
    }
  }

  override def bet(amount: String): Unit = {
    if (game.getState == GameState.Betting) {
      try {
        if (game.isValidBet(amount.toInt) && amount.toInt > 0) {
          game = game.betPlayer(amount.toInt)
          notifyObservers(Event.bet)
          saveGame()
        } else {
          notifyObservers(Event.invalidBet)
        }
      } catch {
        case _: NumberFormatException => notifyObservers(Event.invalidCommand)
      }
    } else {
      notifyObservers(Event.invalidCommand)
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
