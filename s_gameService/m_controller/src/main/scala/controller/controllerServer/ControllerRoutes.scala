package controller.controllerServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import com.google.inject.{Guice, Inject, Injector}
import controller.ControllerInterface
import controller.util.KafkaProducer
import model.modelComponent.{GameFactoryInterface, GameInterface}
import serializer.serializerComponent.GameStateSerializer
import serializer.serializerComponent.JSON.JSONSerializer

import java.nio.file.Paths
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ControllerRoutes @Inject()(controller: ControllerInterface)(implicit system: ActorSystem) {

  private val injector: Injector = Guice.createInjector(new ControllerModule)
  private val gameStateSerializer: GameStateSerializer = injector.getInstance(classOf[GameStateSerializer])
  private val gameFactory: GameFactoryInterface = injector.getInstance(classOf[GameFactoryInterface])
  implicit val ec: ExecutionContext = system.dispatcher
  private val kafkaProducer = KafkaProducer("kafka:9092")

  private def checkIfGameExistsInDb(sessionId: String)
                       (implicit system: ActorSystem, ec: ExecutionContext): Future[Boolean] = {
    val url = s"http://persistence_service:8082/persistence/gameExists?key=$sessionId"
    Http().singleRequest(HttpRequest(uri = url)).map { resp =>
      resp.status == StatusCodes.OK
    }
  }

  private def retrieveGameFromDb(sessionId: String)
                          (implicit system: ActorSystem, ec: ExecutionContext): Future[Option[GameInterface]] = {
    val url = s"http://persistence_service:8082/persistence/retrieveGame?key=$sessionId"
    Http().singleRequest(HttpRequest(uri = url)).flatMap { resp =>
      if (resp.status == StatusCodes.OK) {
        resp.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map { body =>
          val jsonString = body.utf8String
          Some(gameStateSerializer.fromString(gameFactory, jsonString))
        }
      } else {
        Future.successful(None)
      }
    }
  }

  private def sendGameUpdate(sessionId: String): Unit = {
    val gameInstance = controller.getGame(sessionId).get
    kafkaProducer.publish("controller.gameUpdate", sessionId, gameStateSerializer.toString(gameInstance))
  }

  private def sendGameTuiUpdate(sessionId: String): Unit = {
    kafkaProducer.publish("controller.tuiUpdate", sessionId, controller.gameToString(sessionId))
  }
  
  val routes: Route =
    concat(
      pathEndOrSingleSlash {
        get {
          parameter("sessionId".?) {
            case None =>
              val sessionId = UUID.randomUUID().toString
              controller.createSession(sessionId)
              redirect(s"/?sessionId=$sessionId", StatusCodes.SeeOther)

            case Some(sessionId) =>
              // Wenn das Spiel in den aktiven Spielen vom Controller ist
              // müssen wir es ganz sicher nicht aus der DB nuckeln
              if(controller.getGame(sessionId).isSuccess)
                getFromFile(Paths.get("m_client/dist/index.html").toFile)
              else {
                // Wenns nicht dann gucken wir obs schon in der Datenbank liegt
                onSuccess(checkIfGameExistsInDb(sessionId)) {
                  case true =>
                    onSuccess(retrieveGameFromDb(sessionId)) { retrievedGame =>
                      println("Successfully retrieved Game")
                      controller.createSession(sessionId, retrievedGame.get) match {
                        case Success(_) =>
                          getFromFile(Paths.get("m_client/dist/index.html").toFile)
                        case Failure(ex) =>
                          complete(StatusCodes.InternalServerError)
                      }
                    }
                  case false =>
                    println("Game doesn't exist in DB")
                    controller.createSession(sessionId)
                    getFromFile(Paths.get("m_client/dist/index.html").toFile)
                }
              }
          }
        }
      },
      pathPrefix("") {
        getFromDirectory(Paths.get("m_client/dist").toFile.getAbsolutePath)
      },
      pathPrefix("createSession" / Segment) { sessionId =>
        post {
          controller.createSession(sessionId) match {
            case Success(_) =>
              sendGameUpdate(sessionId)
              sendGameTuiUpdate(sessionId)
              complete(StatusCodes.OK)
            case Failure(_) =>
              complete(StatusCodes.InternalServerError)
          }
        }
      },
      pathPrefix("game" / Segment) { sessionId =>
        concat(
          path("start") {
            post {
              controller.startGame(sessionId) match {
                case Success(_) =>
                  sendGameUpdate(sessionId)
                  sendGameTuiUpdate(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("addPlayer" / Segment) { name =>
            post {
              controller.addPlayer(sessionId, name) match {
                case Success(_) =>
                  sendGameUpdate(sessionId)
                  sendGameTuiUpdate(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("hit") {
            post {
              controller.hitPlayer(sessionId) match {
                case Success(_) =>
                  sendGameUpdate(sessionId)
                  sendGameTuiUpdate(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("stand") {
            post {
              controller.standPlayer(sessionId) match {
                case Success(_) =>
                  sendGameUpdate(sessionId)
                  sendGameTuiUpdate(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("doubleDown") {
            post {
              controller.doubleDown(sessionId) match {
                case Success(_) =>
                  sendGameUpdate(sessionId)
                  sendGameTuiUpdate(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("bet" / Segment) { amount =>
            post {
              controller.bet(sessionId, amount) match {
                case Success(_) =>
                  sendGameUpdate(sessionId)
                  sendGameTuiUpdate(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("leave") {
            post {
              controller.leavePlayer(sessionId)
              sendGameUpdate(sessionId)
              sendGameTuiUpdate(sessionId)
              complete(StatusCodes.OK)
            }
          },
          path("state") {
            get {
              complete(controller.gameToString(sessionId))
            }
          }
        )
      }
    )
}
