package tui

import scala.io.StdIn
import scala.util.{Try, Success, Failure}

object TUIApp {
  val baseUrl = "http://controller_service:8080/game"

  def main(args: Array[String]): Unit = {
    var running = true

    post("/state")

    while (running) {
      print("\n> ")

      // Safely read input with null handling
      val input = Option(StdIn.readLine()).getOrElse("")
      val trimmed = input.trim

      if (trimmed.isEmpty) {
        // Skip empty input (including null case)
        Thread.sleep(100) // Small delay to prevent busy waiting
      } else {
        val tokens = trimmed.split("\\s+").toList

        tokens match {
          case "exit" :: Nil =>
            running = false

          case "start" :: Nil =>
            post("/start")

          case "add" :: name :: Nil =>
            post(s"/addPlayer/$name")

          case "bet" :: amount :: Nil =>
            post(s"/bet/$amount")

          case "hit" :: Nil =>
            post("/hit")

          case "stand" :: Nil =>
            post("/stand")

          case "doubleDown" :: Nil =>
            post("/doubleDown")

          case "leave" :: Nil =>
            post("/leave")

          case "save" :: id ::Nil =>
            post(s"/save?gameId=$id")

          case "load" :: id ::Nil =>
            post(s"/load?gameId=$id")

          case "state" :: Nil =>
            get("/state")

          case _ =>
            println("Unknown command or wrong arguments.")
        }
      }
    }

    println("TUI stopped.")
  }

  def get(endpoint: String): Unit = {
    Try(requests.get(s"$baseUrl$endpoint")) match {
      case Success(response) =>
        println(response.text())
      case Failure(exception) =>
        println(s"GET request failed: ${exception.getMessage}")
    }
  }

  def post(endpoint: String): Unit = {
    Try(requests.post(s"$baseUrl$endpoint")) match {
      case Success(response) =>
        println(s"Action success: ${response.statusCode}")
        get("/state")
      case Failure(exception) =>
        println(s"POST request failed: ${exception.getMessage}")
        get("/state")
    }
  }
}