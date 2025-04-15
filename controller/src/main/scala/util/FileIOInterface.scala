package util

import model.GameInterface


trait FileIOInterface {
  def load(path: String = ""): GameInterface
  def save(game: GameInterface, path: String = ""): Unit
}
