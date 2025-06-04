package controller

import controller.util.Observable
import model.modelComponent.GameInterface

import scala.util.Try

trait ControllerInterface extends Observable {
  def getGame: GameInterface
  def setGame(other: GameInterface): Unit
  def initializeGame(): Unit
  def startGame(): Try[Unit]
  def addPlayer(name: String): Try[Unit]
  def leavePlayer(): Unit
  def hitPlayer(): Try[Unit]
  def standPlayer(): Try[Unit]
  def doubleDown(): Try[Unit]
  def bet(amount: String): Try[Unit]
  def exit(): Unit
  def toString: String
}
