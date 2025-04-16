package model

import model.modelComponent.{Dealer, Deck, GameState, Player}

trait GameInterface {

  def getIndex: Int
  def getPlayers: List[Player]
  def getDeck: Deck
  def getDealer: Dealer
  def getState: GameState

  def initialize: GameInterface
  def evaluate: GameInterface
  def createPlayer(name: String): Option[GameInterface]
  def leavePlayer(name: String = ""): GameInterface
  def deal: Option[GameInterface]
  def hitDealer: Option[GameInterface]
  def hitPlayer: Option[GameInterface]
  def standPlayer: Option[GameInterface]
  def betPlayer(amount: Int): Option[GameInterface]
  def doubleDownPlayer: Option[GameInterface]
  def startGame: Option[GameInterface]
  def getPlayerOptions: List[String]
  def toString: String
}
