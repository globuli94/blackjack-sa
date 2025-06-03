import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, entity, path, post}
import akka.http.scaladsl.server.Route
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.*
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration.*

object aiServer {
  private val baseUrl = "http://localhost:8080/game"
  private val buffer = 100

  implicit val system: ActorSystem = ActorSystem("ai")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: Materializer = Materializer(system)

  private val route: Route =
    path("notify") {
      post {
        entity(as[String]) { jsonString =>
          try {
            val json = Json.parse(jsonString)
            queue.offer(json) // offer json to the queue -> source
            println(json)
            complete(StatusCodes.OK, "JSON queued")
          } catch {
            case ex: Exception =>
              println("Invalid JSON")
              complete(StatusCodes.BadRequest, "Invalid JSON")
          }
        }
      }
    }

  // queue as SOURCE, processGameFlow as FLOW, sendCommandToController in SINK
  val (queue: SourceQueueWithComplete[JsValue], steamDone: Future[akka.Done]) =
    Source.queue[JsValue](buffer, OverflowStrategy.backpressure)
      .via(processGameFlow)
      .toMat(Sink.foreach(command =>

        command match {
          case "hit" => sendCommandToController(command)
          case "stand" => sendCommandToController(command)
          case _ => println("no valid command or not your turn")
        }

        println(s"[Sink] Processed: $command")
      ))(Keep.both)
      .run()

  // AI logic as flow
  private def processGameFlow: Flow[JsValue, String, _] =
    Flow[JsValue].map { json =>
      val maybeCurrentIdx = (json \ "current_idx").asOpt[Int]
      val maybeGameState = (json \ "state").asOpt[String]

      val maybePlayer: Option[JsValue] = for {
        idx <- maybeCurrentIdx
        players <- (json \ "players").asOpt[Seq[JsValue]]
        if players.isDefinedAt(idx)
      } yield players(idx)

      val isAi = maybePlayer.flatMap(p => (p \ "name").asOpt[String]).exists(_.toLowerCase == "ai")

      (maybeCurrentIdx, maybeGameState, maybePlayer) match {
        case (Some(idx), Some("Started"), Some(_)) if isAi =>
          val handValue = calculateBlackjackValue(json, idx)
          if (handValue < 17) "hit" else "stand"

        case (_, Some(state), _) =>
          s"Ignored game state: $state"

        case _ =>
          "Invalid input"
      }
    }

  private def cardValue(rank: String): Int = rank match {
    case "J" | "Q" | "K" => 10
    case "A" => 11 // initially treat Ace as 11
    case num => num.toInt
  }

  private def calculateBlackjackValue(json: JsValue, playerIdx: Int): Int = {
    val maybeRanks: Option[Seq[String]] = for {
      players <- (json \ "players").asOpt[Seq[JsValue]]
      if players.isDefinedAt(playerIdx)
      cards <- (players(playerIdx) \ "hand" \ "cards").asOpt[Seq[JsValue]]
    } yield cards.map(card => (card \ "rank").as[String])

    maybeRanks match {
      case Some(ranks) =>
        val initialTotal = ranks.map(cardValue).sum
        val aceCount = ranks.count(_ == "A")
        (0 until aceCount).foldLeft(initialTotal) { (total, _) =>
          if (total > 21) total - 10 else total
        }

      case None => 0 // fallback if players or cards not found
    }
  }

  private def sendCommandToController(command: String): Future[HttpResponse] = {
    val payloadJson = Json.obj("command" -> command)
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"$baseUrl/command",
    )
    Http().singleRequest(request)
  }

  def main(args: Array[String]): Unit = {
    val binding = Http().newServerAt("0.0.0.0", 8083).bind(route)

    binding.onComplete {
      case scala.util.Success(binding) =>
        println(s"AI server started at http://0.0.0.0:8083/")
      case scala.util.Failure(exception) =>
        println(s"Failed to bind server: ${exception.getMessage}")
        system.terminate()
    }

    Await.result(system.whenTerminated, Duration.Inf)
  }
}
