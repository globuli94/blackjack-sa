package controllerServer

import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.google.inject.{AbstractModule, Inject}
import controller.ControllerInterface
import net.codingwell.scalaguice.ScalaModule

class ControllerRoutes @Inject() (controller: ControllerInterface) {

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
