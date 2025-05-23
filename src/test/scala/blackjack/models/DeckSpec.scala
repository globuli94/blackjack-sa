package blackjack.models

import model.modelComponent.{Card, Deck}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.Queue
import scala.util.Random

class DeckSpec extends AnyWordSpec with Matchers {

  "A Deck" should {

    "be initialized as empty by default" in {
      val deck = Deck()
      deck.cards shouldBe empty
    }

    "contain 52 cards after shuffling" in {
      val deck = Deck().shuffle
      deck.length shouldBe 52
    }

    "have unique cards after shuffling" in {
      val deck = Deck().shuffle
      deck.unique_cards shouldBe 52
    }

    "draw a card and return a new deck with one less card" in {
      val deck = Deck().shuffle
      val (drawnCard, newDeck) = deck.draw.get

      drawnCard shouldBe a[Card]
      newDeck.length shouldBe (deck.length - 1)
    }
  }
}

