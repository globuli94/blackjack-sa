package controller

import model.GameInterface
import util.Observable

import scala.util.Try

trait ControllerInterface extends Observable {
  def loadGame(): Unit
  def saveGame(): Unit
  def getGame: GameInterface
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
