package tui

import scala.io.StdIn
import scala.util.{Try, Success, Failure}

object TUIApp {
  val baseUrl = "http://0.0.0.0:8080/game"

  def main(args: Array[String]): Unit = {
    println("=== Blackjack TUI ===")
    println("Type a command (start, addPlayer <name>, bet <amount>, hit, stand, doubleDown, leave, save, load, state, quit)")

    var running = true

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
          case "quit" :: Nil =>
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

          case "save" :: Nil =>
            post("/save")

          case "load" :: Nil =>
            post("/load")

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
        println(s"\n--- Game State ---\n${response.text()}")
      case Failure(exception) =>
        println(s"GET request failed: ${exception.getMessage}")
    }
  }

  def post(endpoint: String): Unit = {
    Try(requests.post(s"$baseUrl$endpoint")) match {
      case Success(response) =>
        println(s"Action success: ${response.statusCode}")
      case Failure(exception) =>
        println(s"POST request failed: ${exception.getMessage}")
    }
  }
}