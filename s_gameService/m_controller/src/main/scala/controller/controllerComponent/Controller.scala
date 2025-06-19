package controller.controllerComponent

import com.google.inject.Inject
import controller.ControllerInterface
import controller.util.{Event, Observable}
import model.modelComponent.{GameFactoryInterface, GameInterface}

import scala.collection.concurrent.TrieMap
import scala.util.{Failure, Success, Try}

class Controller @Inject()(gameFactory: GameFactoryInterface)
  extends ControllerInterface with Observable {

  private val sessions = TrieMap.empty[String, GameInterface]

  def createSession(sessionId: String, game: GameInterface = gameFactory()): Try[Unit] = {
    if (sessions.contains(sessionId))
      Failure(new Exception(s"Session '$sessionId' exists already"))
    else {
      sessions.put(sessionId, game)
      notifyObservers(Event.load)
      Success(())
    }
  }

  private def withGame[T](sessionId: String)(fn: GameInterface => Try[T]): Try[T] =
    sessions.get(sessionId)
      .map(fn)
      .getOrElse(Failure(new Exception(s"Couldn't find session '$sessionId'")))

  def getGame(sessionId: String): Try[GameInterface] =
    withGame(sessionId)(g => Success(g))

  def setGame(sessionId: String, other: GameInterface): Try[Unit] =
    withGame(sessionId) { _ =>
      sessions.update(sessionId, other)
      notifyObservers(Event.load)
      Success(())
    }

  def initializeGame(sessionId: String): Try[Unit] =
    withGame(sessionId) { g =>
      val init = g.initialize
      sessions.update(sessionId, init)
      notifyObservers(Event.start)
      Success(())
    }

  def startGame(sessionId: String): Try[Unit] =
    withGame(sessionId) { g =>
      g.startGame match {
        case Some(updated) =>
          sessions.update(sessionId, updated)
          notifyObservers(Event.start)
          Success(())
        case _ =>
          notifyObservers(Event.invalidCommand)
          Failure(Exception("Game can't be started right now"))
      }
    }

  def addPlayer(sessionId: String, name: String): Try[Unit] =
    withGame(sessionId) { g =>
      g.createPlayer(name) match {
        case Some(updated) =>
          sessions.update(sessionId, updated)
          notifyObservers(Event.addPlayer)
          Success(())
        case _ =>
          notifyObservers(Event.invalidCommand)
          Failure(new Exception("Cannot add players right now"))
      }
    }

  def leavePlayer(sessionId: String): Try[Unit] =
    withGame(sessionId) { g =>
      if (g.getPlayers.nonEmpty) {
        sessions.update(sessionId, g.leavePlayer())
        notifyObservers(Event.leavePlayer)
        Success(())
      } else {
        notifyObservers(Event.invalidCommand)
        Failure(new Exception())
      }
    }

  def hitPlayer(sessionId: String): Try[Unit] =
    withGame(sessionId) { g =>
      g.hitPlayer match {
        case Some(updated) =>
          sessions.update(sessionId, updated)
          notifyObservers(Event.hitNextPlayer)
          Success(())
        case _ =>
          notifyObservers(Event.invalidCommand)
          Failure(new Exception("Hit gerade nicht möglich"))
      }
    }

  def standPlayer(sessionId: String): Try[Unit] =
    withGame(sessionId) { g =>
      g.standPlayer match {
        case Some(updated) =>
          sessions.update(sessionId, updated)
          notifyObservers(Event.standNextPlayer)
          Success(())
        case _ =>
          notifyObservers(Event.invalidCommand)
          Failure(Exception("Player can't stand right now"))
      }
    }

  def doubleDown(sessionId: String): Try[Unit] =
    withGame(sessionId) { g =>
      g.doubleDownPlayer match {
        case Some(updated) =>
          sessions.update(sessionId, updated)
          notifyObservers(Event.doubleDown)
          Success(())
        case _ =>
          notifyObservers(Event.invalidBet)
          Failure(Exception("Cannot double down right now"))
      }
    }

  def bet(sessionId: String, amount: String): Try[Unit] =
    withGame(sessionId) { g =>
      Try(amount.toInt).toOption match {
        case Some(v) =>
          g.betPlayer(v) match {
            case Some(updated) =>
              sessions.update(sessionId, updated)
              notifyObservers(Event.bet)
              Success(())
            case _ =>
              notifyObservers(Event.invalidCommand)
              Failure(Exception("Invalid bet amount"))
          }
        case None =>
          notifyObservers(Event.invalidCommand)
          Failure(Exception("Bet was not integer value"))
      }
    }

  def exit(): Unit = sys.exit(0)

  def gameToString(sessionId: String): String =
    sessions.get(sessionId).map(_.toString).getOrElse(s"Session '$sessionId' nicht gefunden")
}
