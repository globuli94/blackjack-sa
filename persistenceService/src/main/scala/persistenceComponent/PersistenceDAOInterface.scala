package persistenceComponent

import validationComponent._
import scala.concurrent.Future

trait PersistenceDAOInterface {
  def init(): Future[Unit]
  def save(gameId: String, gameStateJson: GameState): Future[Unit]
  def get(gameId: String): Future[Option[GameState]]
  def delete(gameId: String): Future[Unit]
}