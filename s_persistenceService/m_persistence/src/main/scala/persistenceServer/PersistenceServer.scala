package persistenceServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import util.KafkaConsumer
import persistenceComponent.postgresPersistence.PostgreSQLPersistence
import slick.jdbc.PostgresProfile.api._
import validationComponent.Validator.parseGameState
import scala.util.{Try, Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}

object PersistenceServer {

  implicit val system: ActorSystem = ActorSystem("blackjack-controller")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: Materializer = Materializer(system)

  val dbConfig = Database.forConfig("slick.db.default")
  val db = new PostgreSQLPersistence(dbConfig)
  db.init()

  def main(args: Array[String]): Unit = {

    val consumer = KafkaConsumer("kafka:9092", "controller.gameUpdate", "persistenceServer")

    consumer.consumeWithHandler { (gameId: String, gameJson: String) =>
      parseGameState(gameJson) match {
        case Success(game) =>
          db.save(gameId, game).onComplete {
            case Success(_) => println("Game state saved via kafka")
            case Failure(e) => println(s"Failed to save game state via kafka: $e")
          }
        case Failure(ex) =>
          println(s"Invalid json structure via kafka: $ex")
      }
    }

    val persistenceRoutes: PersistenceRoutes = PersistenceRoutes(db)
    val routes: Route = persistenceRoutes.routes
    val binding = Http().newServerAt("0.0.0.0", 8082).bind(routes)

    binding.onComplete {
      case scala.util.Success(binding) =>
        println(s"Persistence server started at http://0.0.0.0:8082/")
      case scala.util.Failure(exception) =>
        println(s"Failed to bind server: ${exception.getMessage}")
        system.terminate()
    }

    Await.result(system.whenTerminated, Duration.Inf)
  }
}