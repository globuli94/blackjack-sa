package tui

import scala.io.StdIn
import scala.util.{Try, Success, Failure}
import requests.Response

object TUIApp {
  val baseUrl = "http://localhost:8080"
  var sessionId: Option[String] = None

  def main(args: Array[String]): Unit = {
    initializeSession()
    mainLoop()
    println("TUI stopped.")
  }

  private def initializeSession(): Unit = {
    println("Initializing game session...")
    get("/") match {
      case Some(response) =>
        if (response.statusCode == 303) {
          // Handle redirect to get session ID
          sessionId = extractSessionId(response)
          println(s"New session created: ${sessionId.getOrElse("ERROR")}")
          getState() // Show initial state
        } else {
          println("Unexpected response when creating session")
        }
      case None =>
        println("Failed to initialize session")
    }
  }

  private def extractSessionId(response: Response): Option[String] = {
    response.headers.get("location")
      .flatMap(_.headOption)
      .flatMap(uri => uri.split("sessionId=").lift(1))
  }

  private def mainLoop(): Unit = {
    var running = true

    while (running) {
      print("\n> ")
      Option(StdIn.readLine()).map(_.trim) match {
        case Some("") => // Skip empty input
        case Some("exit") => running = false
        case Some(input) => handleCommand(input)
        case None => // Handle null input (Ctrl+D)
      }
    }
  }

  private def handleCommand(input: String): Unit = {
    val tokens = input.split("\\s+").toList

    tokens match {
      case "help" :: Nil => printHelp()
      case "start" :: Nil => post(s"/game/${sessionId.getOrElse("")}/start")
      case "add" :: name :: Nil => post(s"/game/${sessionId.getOrElse("")}/addPlayer/$name")
      case "bet" :: amount :: Nil => post(s"/game/${sessionId.getOrElse("")}/bet/$amount")
      case "hit" :: Nil => post(s"/game/${sessionId.getOrElse("")}/hit")
      case "stand" :: Nil => post(s"/game/${sessionId.getOrElse("")}/stand")
      case "double" :: Nil => post(s"/game/${sessionId.getOrElse("")}/doubleDown")
      case "leave" :: Nil => post(s"/game/${sessionId.getOrElse("")}/leave")
      case "save" :: Nil => post(s"/game/${sessionId.getOrElse("")}/save")
      case "load" :: Nil => post(s"/game/${sessionId.getOrElse("")}/load")
      case "state" :: Nil => getState()
      case "new" :: Nil => initializeSession()
      case _ => println("Unknown command. Type 'help' for available commands.")
    }
  }

  private def getState(): Unit = {
    sessionId.foreach { id =>
      get(s"/game/$id/state").foreach { response =>
        println(response.text())
      }
    }
  }

  private def printHelp(): Unit = {
    println("Available commands:")
    println("  help          - Show this help")
    println("  start         - Start the game")
    println("  add <name>    - Add a player")
    println("  bet <amount>  - Place a bet")
    println("  hit           - Take a card")
    println("  stand         - Stand with current hand")
    println("  double        - Double down")
    println("  leave         - Leave the game")
    println("  save          - Save game state")
    println("  load          - Load game state")
    println("  state         - Show current game state")
    println("  new           - Create new session")
    println("  exit          - Exit the TUI")
  }

  private def get(endpoint: String): Option[Response] = {
    Try(requests.get(s"$baseUrl$endpoint")) match {
      case Success(response) => Some(response)
      case Failure(e) =>
        println(s"GET request failed: ${e.getMessage}")
        None
    }
  }

  private def post(endpoint: String): Unit = {
    sessionId match {
      case Some(id) =>
        Try(requests.post(s"$baseUrl$endpoint")) match {
          case Success(response) =>
            println(s"Action ${response.statusCode} - ${if (response.statusCode == 200) "Success" else "Failed"}")
            getState()
          case Failure(e) =>
            println(s"POST request failed: ${e.getMessage}")
        }
      case None =>
        println("No active session. Use 'new' to create one.")
    }
  }
}