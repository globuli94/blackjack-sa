package tui

import akka.actor.ActorSystem
import akka.stream.Materializer
import util.KafkaConsumer

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object TUIApp {

  implicit val system: ActorSystem = ActorSystem("tui-service")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: Materializer = Materializer(system)

  private val gameServiceUrl = "http://game_service:8080/game"
  private val persistenceServiceUrl = "http://persistence_service:8081"

  def main(args: Array[String]): Unit = {

    var running = true
    var sessionId: Option[String] = Option.empty
    var gameStateString: Option[String] = Option.empty

    val consumer = KafkaConsumer("kafka:9092", "controller.tuiUpdate", "tuiServer")

    consumer.consumeWithHandler { (gameId: String, stateString: String) =>

      if(sessionId.isEmpty) {
        print("No session selected, ignoring messages")
        return
      }

      if (gameId == sessionId.get) {
        print(stateString)
        gameStateString = Some(stateString)
      }
    }

    print("Select a session with selectSession <sessionId>")

    while (running) {

      if(sessionId.isEmpty)
        print("No session selected")

      print("\n> ")

      val input = Option(StdIn.readLine()).getOrElse("")
      val trimmed = input.trim

      if (trimmed.isEmpty) {
        Thread.sleep(100)
      } else {
        val tokens = trimmed.split("\\s+").toList

        tokens match {
          case "exit" :: Nil =>
            running = false

          case "createSession" :: sessId :: Nil =>
            post("http://game_service:8080", s"/createSession/$sessId")
            sessionId = Some(sessId)

          case "selectSession" :: sessId :: Nil =>
            sessionId = Some(sessId)
            get(s"/$sessId/state")

          case "start" :: Nil =>
            if(sessionId.isEmpty) {
              println("No session selected")
            } else {
              post(gameServiceUrl, s"/${sessionId.get}/start")
            }

          case "add" :: name :: Nil =>
            if(sessionId.isEmpty) {
              println("No session selected")
            } else {
              post(gameServiceUrl, s"/${sessionId.get}/addPlayer/$name")
            }

          case "bet" :: amount :: Nil =>
            if(sessionId.isEmpty) {
              println("No session selected")
            } else {
              post(gameServiceUrl, s"/${sessionId.get}/bet/$amount")
            }

          case "hit" :: Nil =>
            if(sessionId.isEmpty) {
              println("No session selected")
            } else {
              post(gameServiceUrl, s"/${sessionId.get}/hit")
            }

          case "stand" :: Nil =>
            if(sessionId.isEmpty) {
              println("No session selected")
            } else {
              post(gameServiceUrl, s"/${sessionId.get}/stand")
            }

          case "doubleDown" :: Nil =>
            if(sessionId.isEmpty) {
              println("No session selected")
            } else {
              post(gameServiceUrl, s"/${sessionId.get}/doubleDown")
            }

          case "leave" :: Nil =>
            if(sessionId.isEmpty) {
              println("No session selected")
            } else {
              post(gameServiceUrl, s"/${sessionId.get}/leave")
            }

          case _ =>
            println("Unknown command or wrong arguments.")
        }
      }
    }

    println("TUI stopped")
  }

  def get(endpoint: String): Unit = {
    Try(requests.get(s"$gameServiceUrl$endpoint")) match {
      case Success(response) =>
        println(response.text())
      case Failure(exception) =>
        println(s"GET request failed: ${exception.getMessage}")
    }
  }

  def post(baseUrl: String, endpoint: String): Unit = {
    Try(requests.post(s"$baseUrl$endpoint")) match {
      case Success(response) =>
      case Failure(exception) =>
        println(s"POST request failed: ${exception.getMessage}")
    }
  }
}
