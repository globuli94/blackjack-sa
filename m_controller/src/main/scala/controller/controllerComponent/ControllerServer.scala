package controller.controllerComponent

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import com.google.inject.{Guice, Injector}
import akka.http.scaladsl.server.Route
import scala.concurrent.ExecutionContextExecutor

object Server extends App {

  implicit val system: ActorSystem = ActorSystem("blackjack-controller")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: Materializer = Materializer(system)

  val injector: Injector = Guice.createInjector(new BlackjackModule)
  val controllerRoutes: ControllerRoutes = injector.getInstance(classOf[ControllerRoutes])
  val routes: Route = controllerRoutes.routes

  val binding = Http().newServerAt("localhost", 8080).bind(routes)

  binding.onComplete {
    case scala.util.Success(binding) =>
      println(s"Server started at http://localhost:8080/")
    case scala.util.Failure(exception) =>
      println(s"Failed to bind server: ${exception.getMessage}")
      system.terminate()
  }
}
