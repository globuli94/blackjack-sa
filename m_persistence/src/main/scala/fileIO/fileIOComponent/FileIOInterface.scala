package fileIO.fileIOComponent

import model.modelComponent.{GameFactoryInterface, GameInterface}

trait FileIOInterface {
  def load(gameFactory: GameFactoryInterface, path: String = ""): GameInterface
  def save(gameFactory: GameFactoryInterface, game: GameInterface, path: String = ""): Unit
}
