package controller.controllerServer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.google.inject.{Guice, Injector}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}

object ControllerServer {
  private val injector: Injector = Guice.createInjector(new ControllerModule)
  implicit val system: ActorSystem = ActorSystem("blackjack-controller")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: Materializer = Materializer(system)

  def main(args: Array[String]): Unit = {

    val controllerRoutes: ControllerRoutes = injector.getInstance(classOf[ControllerRoutes])
    val routes: Route = controllerRoutes.routes
    val binding = Http().newServerAt("0.0.0.0", 8080).bind(routes)

    binding.onComplete {
      case scala.util.Success(binding) =>
        println(s"Game server started at http://localhost:8080/")
      case scala.util.Failure(exception) =>
        println(s"Failed to bind server: ${exception.getMessage}")
        system.terminate()
    }

    Await.result(system.whenTerminated, Duration.Inf)
  }
}