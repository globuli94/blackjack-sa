package controller.controllerServer

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.google.inject.{Guice, Inject, Injector}
import controller.ControllerInterface
import model.modelComponent.GameFactoryInterface
import serializer.serializerComponent.GameStateSerializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.HttpResponse
import akka.util.ByteString
import akka.actor.ActorSystem

import java.nio.file.Paths
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ControllerRoutes @Inject() (controller: ControllerInterface)(implicit system: ActorSystem) {

  private val persistenceServiceUrl = "http://persistence_service:8082/persistence"
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val injector: Injector = Guice.createInjector(new ControllerModule)
  private val gameStateSerializer: GameStateSerializer = injector.getInstance(classOf[GameStateSerializer])
  private val gameFactory: GameFactoryInterface = injector.getInstance(classOf[GameFactoryInterface])

  val routes: Route =
    pathPrefix("game") {
      concat(
        path("start") {
          post {
            controller.startGame() match {
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.InternalServerError)
            }
          }
        },
        path("addPlayer" / Segment) { name =>
          post {
            controller.addPlayer(name) match {
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.InternalServerError)
            }
          }
        },
        path("hit") {
          post {
            controller.hitPlayer() match {
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.InternalServerError)
            }
          }
        },
        path("stand") {
          post {
            controller.standPlayer() match {
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.InternalServerError)
            }
          }
        },
        path("doubleDown") {
          post {
            controller.doubleDown() match {
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.InternalServerError)
            }
          }
        },
        path("bet" / Segment) { amount =>
          post {
            controller.bet(amount) match {
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.InternalServerError)
            }
          }
        },
        path("leave") {
          post {
            controller.leavePlayer()
            complete(StatusCodes.OK)
          }
        },
        path("save") {
          post {
            parameters("gameId") { gameId =>

              val serializedData = gameStateSerializer.toString(controller.getGame)

              val request = HttpRequest(
                method = HttpMethods.POST,
                uri = s"http://0.0.0.0:8082/persistence/storeGame?key=$gameId",
                entity = HttpEntity(ContentTypes.`application/json`, serializedData)
              )

              val response = Http(system).singleRequest(request)

              onComplete(response) {
                case Success(res) if res.status == StatusCodes.OK =>
                  complete(StatusCodes.OK)
                case _ =>
                  complete(StatusCodes.InternalServerError)
              }
            }
          }
        },
        path("load") {
          post {
            parameters("gameId") { gameId =>
              val request = HttpRequest(
                method = HttpMethods.GET,
                uri = s"http://0.0.0.0:8082/persistence/retrieveGame?key=$gameId"
              )

              val response = Http(system).singleRequest(request)

              onComplete(response) {
                case Success(res: HttpResponse) if res.status == StatusCodes.OK =>
                  onComplete(res.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map(_.utf8String)) {
                    case Success(serializedData) =>
                      val game = gameStateSerializer.fromString(gameFactory, serializedData)
                      controller.setGame(game)
                      complete(StatusCodes.OK)
                    case Failure(_) =>
                      complete(StatusCodes.InternalServerError)
                  }

                case _ => complete(StatusCodes.InternalServerError)
              }
            }
          }
        },
        path("state") {
          get {
            complete(controller.toString)
          }
        }
      )
    } ~
    pathPrefix("") {
      getFromDirectory(Paths.get("m_client/dist").toFile.getAbsolutePath)
    }
}
