package fileIO.fileIOComponent.JSON

import fileIO.fileIOComponent.FileIOInterface
import model.modelComponent.*
import play.api.libs.functional.syntax.*
import play.api.libs.json.*

import java.io.PrintWriter
import scala.io.Source


class FileIOJSON extends FileIOInterface {

  // PLAYER STATE
  implicit val playerStateReads: Reads[PlayerState] = Reads[PlayerState] {
    case JsString("Playing") => JsSuccess(PlayerState.Playing)
    case JsString("Standing") => JsSuccess(PlayerState.Standing)
    case JsString("DoubledDown") => JsSuccess(PlayerState.DoubledDown)
    case JsString("Busted") => JsSuccess(PlayerState.Busted)
    case JsString("Blackjack") => JsSuccess(PlayerState.Blackjack)
    case JsString("WON") => JsSuccess(PlayerState.WON)
    case JsString("LOST") => JsSuccess(PlayerState.LOST)
    case JsString("Betting") => JsSuccess(PlayerState.Betting)
    case JsString("Idle") => JsSuccess(PlayerState.Idle)
    case JsString("Split") => JsSuccess(PlayerState.Split)
    case _ => JsError("Invalid PlayerState")
  }

  // DEALER STATE
  implicit val dealerStateReads: Reads[DealerState] = Reads[DealerState] {
    case JsString("Idle") => JsSuccess(DealerState.Idle)
    case JsString("Dealing") => JsSuccess(DealerState.Dealing)
    case JsString("Bust") => JsSuccess(DealerState.Bust)
    case JsString("Standing") => JsSuccess(DealerState.Standing)
    case _ => JsError("Invalid PlayerState")
  }

  // GAME STATE
  implicit val gameStateReads: Reads[GameState] = Reads[GameState] {
    case JsString("Initialized") => JsSuccess(GameState.Initialized)
    case JsString("Betting") => JsSuccess(GameState.Betting)
    case JsString("Started") => JsSuccess(GameState.Started)
    case JsString("Evaluated") => JsSuccess(GameState.Evaluated)
    case _ => JsError("Invalid PlayerState")
  }


  // CARD
  implicit val cardWrites: Writes[Card] = (card: Card) => Json.obj(
    "rank" -> card.rank,
    "suit" -> card.suit
  )
  implicit val cardReads: Reads[Card] = (
    (JsPath \ "rank").read[String] and
      (JsPath \ "suit").read[String]
    ) ((rank, suit) => Card(rank, suit))

  // HAND
  implicit val handWrites: Writes[Hand] = (hand: Hand) => Json.obj(
    "cards" -> hand.cards,           // Serializes the list of cards
  )
  implicit val handReads: Reads[Hand] = (json: JsValue) => {
    (json \ "cards").validate[List[Card]].map(cards => Hand(cards))
  }        // Create a Hand object from the parsed values

  // PLAYER
  implicit val playerWrites: Writes[Player] = (player: Player) => Json.obj(
    "name" -> player.name,
    "hand" -> player.hand,
    "money" -> player.money,
    "bet" -> player.bet,
    "state" -> player.state.toString
  )
  implicit val playerReads: Reads[Player] = (
    (JsPath \ "name").read[String] and // Reads the list of cards
      (JsPath \ "hand").read[Hand] and
      (JsPath \ "money").read[Int] and
      (JsPath \ "bet").read[Int] and
      (JsPath \ "state").read[PlayerState]
    // Reads the HandState (Play, Stand, etc.)
    ) ((name, hand, money, bet, state) => Player(name, hand, money, bet, state))

  // DEALER
  implicit val dealerWrites: Writes[Dealer] = (dealer: Dealer) => Json.obj(
    "hand" -> dealer.hand,
    "state" -> dealer.state.toString
  )
  implicit val dealerReads: Reads[Dealer] = (
    (JsPath \ "hand").read[Hand] and // Reads the list of cards
      (JsPath \ "state").read[DealerState]
    // Reads the HandState (Play, Stand, etc.)
    )((hand, state) => Dealer(hand, state))

  // DECK
  // DECK
  implicit val deckWrites: Writes[Deck] = (deck: Deck) => Json.obj(
    "cards" -> Json.toJson(deck.cards)
  )

  implicit val deckReads: Reads[Deck] = (json: JsValue) => {
    (json \ "cards").validate[List[Card]].map(cards => Deck(cards))
  }

  // GAME
  implicit val gameWrites: Writes[GameInterface] = (game: GameInterface) => Json.obj(
    "current_idx" -> game.getIndex,
    "players" -> game.getPlayers,
    "deck" -> game.getDeck,
    "dealer" -> game.getDealer,
    "state" -> game.getState.toString
  )

  override def load(gameFactory: GameFactoryInterface, path: String = "game.json"): GameInterface = {
    val source = Source.fromFile(path)

    try {
      val json = Json.parse(source.getLines().mkString)

      val idx = (json \ "current_idx").as[Int]
      val players = (json \ "players").as[List[Player]]
      val deck = (json \ "deck").as[Deck]
      val dealer = (json \ "dealer").as[Dealer]
      val state = (json \ "state").as[GameState]

      gameFactory(idx, players, deck, dealer, state)
    } finally {
      source.close()
    }
  }
  
  override def save(gameFactory: GameFactoryInterface, game: GameInterface, path: String = "game.json"): Unit = {
    val jsonString = Json.stringify(Json.toJson(game))
    new PrintWriter(path) {
      write(jsonString);
      close()
    }
  }
}