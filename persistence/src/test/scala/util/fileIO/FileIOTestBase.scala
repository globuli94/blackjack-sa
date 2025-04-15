package util.fileIO

import model.GameInterface
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.{BufferedSource, Source}

trait FileIOTestBase extends AnyWordSpec with Matchers {
  // Helper method to compare games (ignoring some fields if needed)
  def gamesAreSimilar(g1: GameInterface, g2: GameInterface): Boolean = {
    g1.getIndex == g2.getIndex &&
      g1.getPlayers.size == g2.getPlayers.size &&
      g1.getState == g2.getState
  }
}
