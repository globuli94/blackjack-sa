package persistenceComponent.mongoPersistence

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.ReplaceOptions
import play.api.libs.json._
import validationComponent._
import scala.concurrent.{Future, ExecutionContext}
import persistenceComponent.PersistenceDAOInterface
import scala.util.Try

class MongoPersistence(mongoClient: MongoClient, dbName: String)(implicit ec: ExecutionContext)
  extends PersistenceDAOInterface {

  private val database: MongoDatabase = mongoClient.getDatabase(dbName)
  private val collection: MongoCollection[Document] = database.getCollection("game_states")

  def init(): Future[Unit] = {
    Future.successful(())
  }

  def save(gameId: String, gameState: GameState): Future[Unit] = {
    val json = Json.stringify(Json.toJson(gameState))
    val doc = Document("_id" -> gameId, "json" -> json)

    collection.replaceOne(
      equal("_id", gameId),
      doc,
      ReplaceOptions().upsert(true)
    ).toFuture().map(_ => ())
  }

  def get(gameId: String): Future[Option[GameState]] = {
    collection.find(equal("_id", gameId))
      .first()
      .headOption()
      .map {
        case Some(doc) =>
          Try(Json.parse(doc.getString("json")).as[GameState]).toOption
        case None => None
      }
  }

  def delete(gameId: String): Future[Unit] = {
    collection.deleteOne(equal("_id", gameId))
      .toFuture()
      .map(_ => ())
  }
}