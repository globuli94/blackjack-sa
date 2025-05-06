package persistenceServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}

object PersistenceServer {
  implicit val system: ActorSystem = ActorSystem("blackjack-controller")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: Materializer = Materializer(system)

  def main(args: Array[String]): Unit = {

    val persistenceRoutes: PersistenceRoutes = PersistenceRoutes()
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