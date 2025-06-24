package serializer.serializerComponent

import model.modelComponent.{GameFactoryInterface, GameInterface}

trait GameStateSerializer {
  def fromString(gameFactory: GameFactoryInterface, data: String): GameInterface
  def toString(game: GameInterface): String
}
