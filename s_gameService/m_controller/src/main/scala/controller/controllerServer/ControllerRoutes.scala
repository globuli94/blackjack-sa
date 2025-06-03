package controller.controllerServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import com.google.inject.{Guice, Inject, Injector}
import controller.ControllerInterface
import model.modelComponent.GameFactoryInterface
import serializer.serializerComponent.GameStateSerializer

import java.nio.file.Paths
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ControllerRoutes @Inject()(controller: ControllerInterface)(implicit system: ActorSystem) {
  private val injector: Injector = Guice.createInjector(new ControllerModule)
  private val gameStateSerializer: GameStateSerializer = injector.getInstance(classOf[GameStateSerializer])
  private val gameFactory: GameFactoryInterface = injector.getInstance(classOf[GameFactoryInterface])

  implicit val ec: ExecutionContext = system.dispatcher

  val routes: Route = {
    concat(
      pathEndOrSingleSlash {
        get {
          parameter("sessionId".?) {
            case None =>
              val sessionId = UUID.randomUUID().toString
              controller.createSession(sessionId)
              redirect(s"/?sessionId=$sessionId", StatusCodes.SeeOther)

            case Some(id) =>
              getFromFile(Paths.get("m_client/dist/index.html").toFile)
          }
        }
      },
      pathPrefix("") {
        getFromDirectory(Paths.get("m_client/dist").toFile.getAbsolutePath)
      },
      pathPrefix("game" / Segment) { sessionId =>
        concat(
          path("start") {
            post {
              controller.startGame(sessionId) match {
                case Success(_) =>
                  //sendToAI(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => 
                  complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("addPlayer" / Segment) { name =>
            post {
              controller.addPlayer(sessionId, name) match {
                case Success(_) =>
                  //sendToAI(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("hit") {
            post {
              controller.hitPlayer(sessionId) match {
                case Success(_) =>
                  //sendToAI(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("stand") {
            post {
              controller.standPlayer(sessionId) match {
                case Success(_) =>
                  //sendToAI(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("doubleDown") {
            post {
              controller.doubleDown(sessionId) match {
                case Success(_) =>
                  //sendToAI(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("bet" / Segment) { amount =>
            post {
              controller.bet(sessionId, amount) match {
                case Success(_) =>
                  //sendToAI(sessionId)
                  complete(StatusCodes.OK)
                case Failure(_) => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("leave") {
            post {
              controller.leavePlayer(sessionId)
              //sendToAI(sessionId)
              complete(StatusCodes.OK)
            }
          },
          path("save") {
            post {
              val serialized = controller.getGame(sessionId) match {
                case Success(game) => gameStateSerializer.toString(game)
                case Failure(ex)  => ""
              }
              val req = HttpRequest(
                method = HttpMethods.POST,
                uri = s"http://persistence_service:8082/persistence/storeGame?key=$sessionId",
                entity = HttpEntity(ContentTypes.`application/json`, serialized)
              )
              onComplete(Http(system).singleRequest(req)) {
                case Success(resp) if resp.status == StatusCodes.OK => complete(StatusCodes.OK)
                case _ => complete(StatusCodes.InternalServerError)
              }
            }
          },
          path("load") {
            post {
              val req = HttpRequest(
                method = HttpMethods.GET,
                uri = s"http://persistence_service:8082/persistence/retrieveGame?key=$sessionId"
              )
              onComplete(Http(system).singleRequest(req)) {
                case Success(resp) if resp.status == StatusCodes.OK =>
                  onComplete(resp.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map(_.utf8String)) {
                    case Success(data) =>
                      val game = gameStateSerializer.fromString(gameFactory, data)
                      controller.setGame(sessionId, game)
                      complete(StatusCodes.OK)
                    case Failure(_) => complete(StatusCodes.InternalServerError)
                  }
                case _ => complete(StatusCodes.InternalServerError)
              }
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
}
  