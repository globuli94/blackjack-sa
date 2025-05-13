package serializer

import model.{GameFactoryInterface, GameInterface}

trait GameStateSerializer {
  def fromString(gameFactory: GameFactoryInterface, data: String): GameInterface
  def toString(game: GameInterface): String
}
