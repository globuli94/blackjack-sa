package controller.controllerServer

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import controller.ControllerInterface
import scala.util.{Success, Failure}

class ControllerRoutes @Inject() (controller: ControllerInterface) {

  val routes: Route =
    pathPrefix("game") {
      concat(
        path("start") {
          post {
            controller.startGame() match
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.InternalServerError)
          }
        },
        path("addPlayer" / Segment) { name =>
          post {
            controller.addPlayer(name) match
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.BadRequest)
          }
        },
        path("hit") {
          post {
            controller.hitPlayer() match
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.BadRequest)
          }
        },
        path("stand") {
          post {
            controller.standPlayer() match
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.BadRequest)
          }
        },
        path("doubleDown") {
          post {
            controller.doubleDown() match
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.BadRequest)
          }
        },
        path("bet" / Segment) { amount =>
          post {
            controller.bet(amount) match
              case Success(_) => complete(StatusCodes.OK)
              case Failure(_) => complete(StatusCodes.BadRequest)
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
            controller.saveGame()
            complete(StatusCodes.OK)
          }
        },
        path("load") {
          post {
            controller.loadGame()
            complete(StatusCodes.OK)
          }
        },
        path("state") {
          get {
            complete(controller.toString)
          }
        }
      )
    }
}
