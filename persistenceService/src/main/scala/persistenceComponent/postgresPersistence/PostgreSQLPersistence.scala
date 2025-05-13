package persistenceComponent.postgresPersistence

import slick.jdbc.PostgresProfile.api.*
import scala.concurrent.{Future, ExecutionContext}
import play.api.libs.json.*
import validationComponent._
import scala.util.Try
import persistenceComponent.PersistenceDAOInterface
import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api._

class PostgreSQLPersistence(db: Database)(using ec: ExecutionContext) extends PersistenceDAOInterface {

  private class GameStatesTable(tag: Tag) extends Table[(String, String)](tag, "game_states") {
    def gameId = column[String]("game_id", O.PrimaryKey)
    def json = column[String]("json")
    def * = (gameId, json)
  }

  private val gameStates = TableQuery[GameStatesTable]

  def init(): Future[Unit] =
    db.run(gameStates.schema.createIfNotExists)

  def save(gameId: String, gameState: GameState): Future[Unit] = {
    val json = Json.stringify(Json.toJson(gameState))
    val action = gameStates.insertOrUpdate((gameId, json))
    db.run(action).map(_ => ())
  }

  def get(gameId: String): Future[Option[GameState]] = {
    val query = gameStates.filter(_.gameId === gameId).result.headOption
    db.run(query).map {
      case Some((_, json)) =>
        Try(Json.parse(json).as[GameState]).toOption
      case None => None
    }
  }

  def delete(gameId: String): Future[Unit] = {
    val action = gameStates.filter(_.gameId === gameId).delete
    db.run(action).map(_ => ())
  }
}
