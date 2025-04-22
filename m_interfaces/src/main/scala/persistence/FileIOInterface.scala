package persistence

import model.{GameFactoryInterface, GameInterface}


trait FileIOInterface {
  def load(gameFactory: GameFactoryInterface, path: String = ""): GameInterface
  def save(gameFactory: GameFactoryInterface, game: GameInterface, path: String = ""): Unit
}
