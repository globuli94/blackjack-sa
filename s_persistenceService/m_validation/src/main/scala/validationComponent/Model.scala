package validationComponent

import play.api.libs.json._

case class Card(rank: String, suit: String)
case class Hand(cards: List[Card])
case class Dealer(hand: Hand, state: String)
case class Deck(cards: List[Card])
case class Player(name: String, hand: Hand, money: Int, bet: Int, state: String)
case class GameState(
                      current_idx: Int,
                      dealer: Dealer,
                      deck: Deck,
                      players: List[Player],
                      state: String
                    )

implicit val cardFormat: Format[Card] = Json.format[Card]
implicit val handFormat: Format[Hand] = Json.format[Hand]
implicit val dealerFormat: Format[Dealer] = Json.format[Dealer]
implicit val deckFormat: Format[Deck] = Json.format[Deck]
implicit val playerFormat: Format[Player] = Json.format[Player]
implicit val gameStateFormat: Format[GameState] = Json.format[GameState]
