package persistenceServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.google.inject.{Guice, Injector}
import main.BlackjackModule

import scala.concurrent.ExecutionContextExecutor

object PersistenceServer extends App {

  implicit val system: ActorSystem = ActorSystem("blackjack-controller")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: Materializer = Materializer(system)

  def main(args: Array[String], injector: Injector): Unit = {

    val persistenceRoutes: PersistenceRoutes = injector.getInstance(classOf[PersistenceRoutes])
    val routes: Route = persistenceRoutes.routes
    val binding = Http().newServerAt("localhost", 8081).bind(routes)

    binding.onComplete {
      case scala.util.Success(binding) =>
        println(s"Persistence server started at http://localhost:8081/")
      case scala.util.Failure(exception) =>
        println(s"Failed to bind server: ${exception.getMessage}")
        system.terminate()
    }
  }
}
