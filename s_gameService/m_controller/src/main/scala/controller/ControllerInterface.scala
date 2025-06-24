package controller

import controller.util.Observable
import model.modelComponent.GameInterface

import scala.util.Try

trait ControllerInterface extends Observable {
  def createSession(sessionId: String, game: GameInterface = ???): Try[Unit]
  def getGame(sessionId: String): Try[GameInterface]
  def setGame(sessionId: String, other: GameInterface): Try[Unit]
  def initializeGame(sessionId: String): Try[Unit]
  def startGame(sessionId: String): Try[Unit]
  def addPlayer(sessionId: String, name: String): Try[Unit]
  def leavePlayer(sessionId: String): Try[Unit]
  def hitPlayer(sessionId: String): Try[Unit]
  def standPlayer(sessionId: String): Try[Unit]
  def doubleDown(sessionId: String): Try[Unit]
  def bet(sessionId: String, amount: String): Try[Unit]
  def exit(): Unit
  def gameToString(sessionId: String): String
}
