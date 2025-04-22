package persistenceServer

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import com.google.inject.{AbstractModule, Inject}
import controller.ControllerInterface
import fileIO.FileIOInterface
import net.codingwell.scalaguice.ScalaModule

class PersistenceRoutes @Inject()(controller: ControllerInterface, fileIo: FileIOInterface) {

  val routes: Route =
    pathPrefix("persistence") {
      concat(
        path("load" / Segment ) { path =>
          post {
            val game = fileIo.load(path)
            controller.setGame(game)
            complete(StatusCodes.OK)
          }
        },
        path("save" / Segment) { path =>
          post {
            fileIo.save(controller.getGame, path)
            complete(StatusCodes.OK)
          }
        },
      )
    }
}
