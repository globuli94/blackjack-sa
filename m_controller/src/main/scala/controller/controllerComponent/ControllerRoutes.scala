package controller.controllerComponent

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controller.controllerComponent.Controller
import javax.inject.Inject

class ControllerRoutes @Inject() (controller: Controller) {

  val routes: Route =
    pathPrefix("game") {
      concat(
        path("start") {
          post {
            complete(controller.startGame().toString)
          }
        },
        path("addPlayer" / Segment) { name =>
          post {
            complete(controller.addPlayer(name).toString)
          }
        },
        path("hit") {
          post {
            complete(controller.hitPlayer().toString)
          }
        },
        path("stand") {
          post {
            complete(controller.standPlayer().toString)
          }
        },
        path("doubleDown") {
          post {
            complete(controller.doubleDown().toString)
          }
        },
        path("bet" / Segment) { amount =>
          post {
            complete(controller.bet(amount).toString)
          }
        },
        path("leave") {
          post {
            complete {
              controller.leavePlayer()
              "Player left"
            }
          }
        },
        path("save") {
          post {
            complete {
              controller.saveGame()
              "Game saved"
            }
          }
        },
        path("load") {
          post {
            complete {
              controller.loadGame()
              "Game loaded"
            }
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
