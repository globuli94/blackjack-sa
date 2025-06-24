package models

import model.modelComponent.GameState.{Evaluated, Initialized, Started}
import model.modelComponent.PlayerState.*
import model.modelComponent.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameSpec extends AnyWordSpec with Matchers {

  "A Game" should {

    "initialize a game" in {
      val game = Game()

      game.initialize shouldEqual(Game())
    }

    "initially have no players and a new dealer" in {
      val game = Game()

      game.getPlayers shouldBe empty
      game.dealer should equal(new Dealer())
      game.state shouldBe GameState.Initialized
    }

    "correctly add players up to the maximum of 4" in {
      val game = Game()
      val game1 = game.createPlayer("Alice").get
      val game2 = game1.createPlayer("Bob").get
      val game3 = game2.createPlayer("Charlie").get
      val game4 = game3.createPlayer("David").get
      val game5 = game4.createPlayer("Eve") // Should not add a 5th player

      game5 shouldBe None
      game4.getPlayers.map(_.name) should contain allElementsOf List("Alice", "Bob", "Charlie", "David")
    }

    "correctly remove the current player and adjust index" in {
      val player1 = Player("Alice")
      val player2 = Player("Bob")
      val player3 = Player("Charlie")
      val game = Game(players = List(player1, player2, player3), current_idx = 2)

      val updatedGame = game.leavePlayer() // Should remove Charlie

      updatedGame.getPlayers should not contain player3
      updatedGame.getPlayers should contain allElementsOf List(player1, player2)
      updatedGame.getIndex shouldBe 0 // Index resets

      val empty_game = Game(players = List(player1)).leavePlayer()
      empty_game.getPlayers.length shouldBe 0
    }

    "correctly remove a player by name" in {
      val player1 = Player("Player1", money = 100)
      val player2 = Player("Player2", money = 100)
      val game = Game(players = List(player1, player2))

      val newGame = game.leavePlayer("Player1")

      newGame.getPlayers should contain theSameElementsAs List(player2)
    }

    "deal cards to each player and the dealer" in {
      val game = Game(players = List(Player(name = "Steve", state = Betting)), state = GameState.Betting)
      val game_invalid = game.copy(state = Initialized)
      val dealtGame = game.deal.get

      game_invalid.deal should be(None)
      dealtGame.getState shouldBe GameState.Started
    }

    "hit dealer when hand value is less than 17" in {
      val initialDealerHand = Hand(List(Card("5", "Hearts"), Card("6", "Diamonds")))// Value = 11 (Must hit)
      val game = Game().copy(dealer = Dealer(initialDealerHand), deck = Deck(cards = List(Card("6", "Diamonds")))) // Next card = 7
      val invalid_game = Game()
      val updatedGame = game.hitDealer.get

      updatedGame.getDealer.hand.cards.length == 3
      invalid_game.hitDealer should be(None)
    }

    "correctly allow a player to hit" in {
      val player = Player("Alice", hand = Hand(List(Card("5", "Hearts"), Card("6", "Diamonds"))))
      val game = Game(players = List(player), deck = Deck().shuffle)
      val invalid_game = Game()
      val game_no_cards = game.copy(players = List(player), deck = Deck())

      val updatedGame = game.hitPlayer.get

      invalid_game.hitPlayer should be(None)
      game_no_cards.hitPlayer should be(None)
      updatedGame.getPlayers.head.hand.cards.length == 3
    }

    "mark a player as busted when they exceed 21" in {
      val player = Player("Alice", hand = Hand(List(Card("10", "Hearts"), Card("8", "Diamonds"), Card("5", "Clubs"))), state = PlayerState.Playing)
      val player2 = Player("Steve", state = PlayerState.Playing)
      val game = Game(players = List(player, player2), deck = Deck().shuffle, state = GameState.Started)

      val updatedGame = game.hitPlayer.get

      updatedGame.getPlayers.head.state shouldBe PlayerState.Busted
    }

    "mark a player as blackjack when they have 21" in {
      val player = Player("Alice", hand = Hand(List(Card("10", "Hearts"), Card("A", "Diamonds"))))
      val player2 = Player("Steve", state = PlayerState.Playing)
      val game = Game(players = List(player, player2), deck = Deck().shuffle, state = GameState.Started)

      val updatedGame = game.evaluate

      updatedGame.getPlayers.head.state shouldBe PlayerState.Blackjack
    }

    "allow a player to stand and leave all other players the same" in {
      val player = Player("Alice", state = Playing)
      val player2 = Player("Steve", state = Playing)
      val game = Game(players = List(player, player2), state = GameState.Started)

      val updatedGame = game.standPlayer.get

      updatedGame.getPlayers.head.state shouldBe PlayerState.Standing
      updatedGame.getPlayers(1).state shouldBe PlayerState.Playing
    }

    "allow a player to bet and subtract money correctly" in {
      val player = Player("Alice", money = 100, state = PlayerState.Betting)
      val player2 = Player("Steve", state = PlayerState.Betting)
      val game = Game(state = GameState.Betting)
      val game_with_1 = Game(players = List(player), state = GameState.Betting)
      val game_with_2 = Game(players = List(player, player2), state = GameState.Betting)

      val updatedGame_with_1 = game_with_1.betPlayer(20).get
      val updatedGame_with_2 = game_with_2.betPlayer(20).get

      game.betPlayer(10) should be(None)

      updatedGame_with_1.getIndex shouldBe 0

      updatedGame_with_2.getPlayers.head.state shouldBe PlayerState.Playing
      updatedGame_with_2.getPlayers(1) should equal(player2)
      updatedGame_with_2.getIndex shouldBe game_with_2.getIndex + 1
    }

    "allow a player to double down" in {
      val player =
        Player(
          "Alice",
          money = 100,
          bet = 20,
          hand = Hand(List(Card("5", "Hearts"),Card("4", "Hearts"))),
          state = PlayerState.Playing)
      val player2 =
        Player("Steve", state = PlayerState.Playing)

      // double down on game without player
      val game_empty = Game(state = GameState.Started)
      game_empty.doubleDownPlayer should equal(None)

      // game with invalid game state
      val game_invalid_state = Game(state = GameState.Initialized)
      game_invalid_state.doubleDownPlayer should equal(None)

      // cant double down or not enough money
      val game_cant_double_down =
        Game(players = List(player2), state = GameState.Started)
      game_cant_double_down.doubleDownPlayer should equal(None)

      // game with empty deck
      val game_empty_deck =
        Game(
          players = List(player, player2),
          deck = Deck(),
          state = GameState.Started
        )
      game_empty_deck.doubleDownPlayer should be(None)

      // can double down and set index when game correct
      val game_with_valid_players =
        Game(
          current_idx = 1,
          players = List(player2, player),
          deck = Deck(List(Card("5", "Hearts"), Card("5", "Hearts"))),
          state = GameState.Started
        )
      game_with_valid_players.doubleDownPlayer.get.getPlayers(1).state should be(PlayerState.DoubledDown)
      game_with_valid_players.doubleDownPlayer.get.getIndex should be(1)
    }

    "evaluate win or loss depending on cards and player states" should {

      val game = Game().createPlayer("Alfred").get
      val dealer = Dealer(Hand(List(Card("10", "Hearts"), Card("8", "Diamonds"))))

      "eval correctly if player and dealer have blackjack" in {
        val player = Player("Alice", state = Blackjack)
        val dealer = Dealer(hand = Hand(cards = List(Card("A", "Hearts"), Card("K", "Hearts"))))

        val evaluated_game =
          Game(game.getIndex, List(player), game.getDeck, dealer, GameState.Started).evaluate

        evaluated_game.getPlayers.head.state shouldBe(PlayerState.LOST)
      }

      "eval correctly if player has blackjack" in {
        val player = Player("Alice", state = Blackjack)
        val dealer = Dealer(hand = Hand(cards = List(Card("7", "Hearts"), Card("10", "Hearts"))))

        val evaluated_game =
          Game(game.getIndex, List(player), game.getDeck, dealer, GameState.Started).evaluate

        evaluated_game.getPlayers.head.state shouldBe (PlayerState.WON)
      }

      "eval correctly if player is bust" in {
        val player = Player("Alice", state = Busted)
        val dealer = Dealer(hand = Hand(cards = List(Card("7", "Hearts"), Card("10", "Hearts"), Card("7", "Hearts"))))

        val evaluated_game =
          Game(game.getIndex, List(player), game.getDeck, dealer, GameState.Started).evaluate

        evaluated_game.getPlayers.head.state shouldBe (PlayerState.LOST)
      }

      "eval correctly if player has lost standing" in {
        val player = Player("Alice", state = Standing, hand = Hand(List(Card("2", "Hearts"), Card("2", "Hearts"))))
        val dealer =
          Dealer(hand = Hand(cards = List(Card("A", "Hearts"), Card("7", "Hearts"))), state = DealerState.Standing)

        val evaluated_game =
          Game(game.getIndex, List(player), game.getDeck, dealer, GameState.Started).evaluate

        evaluated_game.getPlayers.head.state shouldBe (PlayerState.LOST)
      }

      "eval correctly if player has won standing" in {
        val player =
          Player("Alice", state = Standing, hand = Hand(List(Card("10", "Hearts"), Card("10", "Hearts"))))
        val dealer =
          Dealer(hand = Hand(cards = List(Card("A", "Hearts"), Card("7", "Hearts"))), state = DealerState.Standing)

        val evaluated_game =
          Game(game.getIndex, List(player), game.getDeck, dealer, GameState.Started).evaluate

        evaluated_game.getPlayers.head.state shouldBe (PlayerState.WON)
      }

      "eval correctly if player has won double down" in {
        val player = Player("Alice", state = DoubledDown, hand = Hand(List(Card("10", "Hearts"), Card("10", "Hearts"))))
        val dealer = Dealer(hand = Hand(cards = List(Card("A", "Hearts"), Card("7", "Hearts"))))

        val evaluated_game =
          Game(game.getIndex, List(player), game.getDeck, dealer, GameState.Started).evaluate

        evaluated_game.getPlayers.head.state shouldBe (PlayerState.WON)
      }
    }

    "provide the correct player options" should {
      val player = Player("Alice", state = PlayerState.LOST)

      "always have option exit" in {
        val game = Game(players = List(player))
        val options = game.getPlayerOptions

        options should contain("exit")
      }

      "not contain start if there are no players" in {
        val game = Game()
        val options = game.getPlayerOptions

        options should not contain("start")
      }

      "have the option add <player> when the game is initialized and has players" in {
        val game = Game(players = List(player))
        val options = game.getPlayerOptions

        options should contain("add <player>")
      }

      "have the option start when the game is initialized and has players" in {
        val game = Game(players = List(player))
        val options = game.getPlayerOptions

        options should contain("start")
      }

      "have the option to bet and leave when in game is in state betting" in {
        val game = Game(state = GameState.Betting)
        val options = game.getPlayerOptions

        options should contain("bet <amount>")
      }

      "when the game state is started" should {

        "have the option to stand" in {
          val game = Game(players = List(Player("Steve")), state = Started)
          val options = game.getPlayerOptions

          options should contain("stand")
        }

        "have to option to hit when hand allows hit" in {
          val hand = Hand(cards = List(Card("10", "Hearts"), Card("10", "Hearts")))
          val game = Game(players = List(Player("Steve",  hand = hand)), state = Started)
          val options = game.getPlayerOptions

          options should contain("hit")
        }

        "have no option to hit when hand doesnt allow hit" in {
          val hand = Hand(cards = List(Card("10", "Hearts"), Card("10", "Hearts"), Card("10", "Hearts")))
          val game = Game(players = List(Player("Steve", hand = hand)), state = Started)
          val options = game.getPlayerOptions

          options should not contain("hit")
        }

        "have to option to double down when hand allows hit" in {
          val hand = Hand(cards = List(Card("5", "Hearts"), Card("5", "Hearts")))
          val game = Game(players = List(Player("Steve",  hand = hand)), state = Started)
          val options = game.getPlayerOptions

          options should contain("double (down)")
        }

        "return base options if there is no player" in {
          val game = Game(state = Started)
          val options = game.getPlayerOptions

          options should contain("exit")
        }
      }

      "when the game is evaluated have the option to continue " in {
        val game = Game(state = Evaluated)
        val options = game.getPlayerOptions

        options should contain("continue")
      }
    }

    "reset to initialized state after evaluation" in {
      val player = Player("Alice", state = PlayerState.LOST)
      val game = Game(players = List(player), state = GameState.Evaluated)

      val newGame = game.evaluate

      newGame.getState shouldBe GameState.Initialized
    }
  }

  "Game toString" should {

    "display initial game state correctly" in {
      val game = Game(state = Initialized)
      val output = game.toString

      output should include("State: Initialized")
      output should include("Dealer")
      output should include("Table")
      output should include("Options:")
    }

    "display initial game state correctly with players" in {
      val player = Player("Steve")
      val game = Game(state = Initialized, players = List(player))
      val output = game.toString

      output should include("Player:")
      output should include("Bank:")
      output should include("State:")
    }

    "display dealer and player hands when game has started" in {
      val player = Player("Alice", hand = Hand(List(Card("10", "Hearts"), Card("A", "Diamonds"))))
      val dealer = Dealer(hand = Hand(List(Card("10", "Spades"))))
      val game = Game(players = List(player), dealer = dealer, state = GameState.Started)

      val output = game.toString

      output should include("State: Started")
      output should include("Alice")
      output should include("Dealer")
      output should include("[♥ 10][♦ A]") // Example expected card representation
    }

    "display dealer and player hands when game has started (dealer has blackjack)" in {
      val player = Player("Alice", hand = Hand(List(Card("10", "Hearts"), Card("A", "Diamonds"))))
      val dealer = Dealer(hand = Hand(List(Card("10", "Spades"), Card("A", "Spades"))))
      val game = Game(players = List(player), dealer = dealer, state = GameState.Started)

      val output = game.toString

      output should include("Blackjack")
    }

    "display dealer and player hands when game has started (dealer is bust)" in {
      val player = Player("Alice", hand = Hand(List(Card("10", "Hearts"), Card("A", "Diamonds"))))
      val dealer = Dealer(hand = Hand(List(Card("10", "Spades"), Card("10", "Spades"), Card("10", "Spades"))))
      val game = Game(players = List(player), dealer = dealer, state = GameState.Started)

      val output = game.toString

      output should include("Busted")
    }

    "display betting state correctly" in {
      val player = Player("Alice", money = 100, bet = 20, state = PlayerState.Betting)
      val game = Game(players = List(player), state = GameState.Betting)

      val output = game.toString

      output should include("State: Betting")
      output should include("Bet: $20")
      output should include("Bank: $100")
    }

    "display game evaluation correctly" in {
      val player = Player("Alice", hand = Hand(List(Card("A", "Spades"), Card("K", "Hearts"))), state = PlayerState.Blackjack)
      val game = Game(players = List(player), state = GameState.Evaluated)

      val output = game.toString

      output should include("State: Evaluated")
      output should include("Blackjack")
      output should include("Alice")
    }

    "highlight the current player correctly" in {
      val player1 = Player("Alice", hand = Hand(List(Card("10", "Diamonds"), Card("6", "Clubs"))))
      val player2 = Player("Bob", hand = Hand(List(Card("8", "Hearts"), Card("7", "Spades"))))
      val game = Game(players = List(player1, player2), current_idx = 1, state = GameState.Started)

      val output = game.toString

      output should include("Current Player: Bob")
      output should include("[♦ 10][♣ 6]")
      output should include("[♥ 8][♠ 7]")
    }
  }
}
