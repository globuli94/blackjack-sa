package fileIO.persistenceServer

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.google.inject.{AbstractModule, Inject}
import controller.ControllerInterface
import model.GameFactoryInterface
import persistence.FileIOInterface

class PersistenceRoutes @Inject()(controller: ControllerInterface, fileIo: FileIOInterface, gameFactory: GameFactoryInterface) {

  val routes: Route =
    pathPrefix("persistence") {
      concat(
        path("load" / Segment ) { path =>
          post {
            val game = fileIo.load(gameFactory, path)
            controller.setGame(game)
            complete(StatusCodes.OK)
          }
        },
        path("save" / Segment) { path =>
          post {
            fileIo.save(gameFactory, controller.getGame, path)
            complete(StatusCodes.OK)
          }
        },
      )
    }
}
