package persistenceServer

import akka.http.scaladsl.model.{StatusCodes, HttpEntity, ContentTypes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import validationComponent.Validator.parseGameState
import validationComponent._
import scala.util.{Try, Success, Failure}
import slick.jdbc.PostgresProfile.api._
import persistenceComponent.postgresPersistence.PostgreSQLPersistence
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json

class PersistenceRoutes {

  val db = Database.forConfig("slick.db.default")
  private val persistence = new PostgreSQLPersistence(db)
  persistence.init()

  val routes: Route =
    pathPrefix("persistence") {
      concat(
        path("storeGame") {
          parameter("key") { key =>
            entity(as[String]) { jsonString =>
              parseGameState(jsonString) match {
                case Success(game) => {
                  onComplete(persistence.save(key, game)) {
                    case Success(_) => complete(StatusCodes.OK)
                    case Failure(_) => complete(StatusCodes.InternalServerError, "Saving failed")
                  }
                }
                case Failure(ex) => complete(StatusCodes.BadRequest, "Invalid json structure")
              }
            }
          }
        },
        path("retrieveGame") {
          parameter("key") { key =>
            onSuccess(persistence.get(key)) {
              case Some(gameState) =>
                complete(HttpEntity(ContentTypes.`application/json`, Json.stringify(Json.toJson(gameState))))
              case None =>
                complete(StatusCodes.NotFound, s"No game found for key: $key")
            }
          }
        }
      )
    }
}
