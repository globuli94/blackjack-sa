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
import persistenceComponent.PersistenceDAOInterface

class PersistenceRoutes(db: PersistenceDAOInterface) {

  val routes: Route =
    pathPrefix("persistence") {
      concat(
        path("gameExists") {
          parameter("key") { key =>
            onSuccess(db.exists(key)) {
              case true  => complete(StatusCodes.OK, "true")
              case false => complete(StatusCodes.NotFound, "false")
            }
          }
        },
        path("storeGame") {
          parameter("key") { key =>
            entity(as[String]) { jsonString =>
              parseGameState(jsonString) match {
                case Success(game) => {
                  onComplete(db.save(key, game)) {
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
            onSuccess(db.get(key)) {
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
