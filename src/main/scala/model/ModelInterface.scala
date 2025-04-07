package model

import model.modelComponent.{Dealer, Deck, GameState, Player}

import java.util.Optional

trait ModelInterface {
  
  def getIndex: Int
  def getPlayers: List[Player]
  def getDeck: Deck
  def getDealer: Dealer
  def getState: GameState

  def initialize: Optional[ModelInterface]
  def evaluate: ModelInterface
  def createPlayer(name: String): Optional[ModelInterface]
  def leavePlayer(name: String = ""): Optional[ModelInterface]
  def deal: Optional[ModelInterface]
  def hitDealer: ModelInterface
  def hitPlayer: ModelInterface
  def standPlayer: ModelInterface
  def betPlayer(amount: Int): ModelInterface
  def isValidBet(amount: Int): Boolean
  def doubleDownPlayer: ModelInterface
  def startGame: ModelInterface
  def getPlayerOptions: List[String]
  def toString: String
}
