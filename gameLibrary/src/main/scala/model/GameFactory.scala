package model

class GameFactory extends GameFactoryInterface {
  def apply(idx: Int, players: List[Player], deck: Deck, dealer: Dealer, state: GameState): GameInterface = {
    Game(idx, players, deck, dealer, state)
  }
}
