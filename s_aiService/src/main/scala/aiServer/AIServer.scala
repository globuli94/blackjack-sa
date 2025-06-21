package aiServer

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.json._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.concurrent.Await

object AIServer {

  implicit val system: ActorSystem = ActorSystem("ai-service")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: Materializer = Materializer(system)

  private val baseUrl = "http://game_service:8080/game"

  def main(args: Array[String]): Unit = {

    val consumer = KafkaConsumer("kafka:9092", "controller.gameUpdate", "aiServer")

    consumer.consumeWithHandler { (gameId: String, stateString: String) =>
      try {
        val json = Json.parse(stateString)

        val maybeCurrentIdx = (json \ "current_idx").asOpt[Int]
        val maybeGameState = (json \ "state").asOpt[String]

        val maybePlayer: Option[JsValue] = for {
          idx <- maybeCurrentIdx
          players <- (json \ "players").asOpt[Seq[JsValue]]
          if players.isDefinedAt(idx)
        } yield players(idx)

        val isAi = maybePlayer
          .flatMap(p => (p \ "name").asOpt[String])
          .exists(_.toLowerCase.startsWith("ai"))
        val maybeMoney = maybePlayer.flatMap(p => (p \ "money").asOpt[Int])

        val commandStr = (maybeCurrentIdx, maybeGameState, maybePlayer, maybeMoney) match {
          case (Some(idx), Some("Started"), Some(_), Some(_)) if isAi =>
            val handValue = calculateBlackjackValue(json, idx)
            if (handValue < 17) "hit" else "stand"

          case (Some(idx), Some("Betting"), Some(_), Some(money)) if isAi =>
            if (money > 0) {
              val bet = money / 2
              s"bet/$bet"
            } else {
              "leave"
            }
          case (_, Some(state), _, _) =>
            s"Ignored game state: $state"

          case _ =>
            "Invalid input"
        }

        commandStr match {
          case "hit" | "stand" =>
            println(s"[AI] Will POST $baseUrl/$gameId/$commandStr in 1 second")
            system.scheduler.scheduleOnce(1.seconds) {
              postCommand(s"$baseUrl/$gameId/$commandStr")
            }
          case cmd if cmd.startsWith("bet") =>
            println(s"[AI] Will POST $baseUrl/$gameId/$cmd in 1 second")
            system.scheduler.scheduleOnce(1.seconds) {
              postCommand(s"$baseUrl/$gameId/$cmd")
            }
          case "leave" =>
            println(s"[AI] Will POST $baseUrl/$gameId/leave in 3 second")
            system.scheduler.scheduleOnce(1.seconds) {
              postCommand(s"$baseUrl/$gameId/leave")
            }
          case _ =>
            println(s"[AI] Ignored or invalid: $commandStr")
        }

      } catch {
        case ex: Exception =>
          println(s"[AI] Failed to parse state: ${ex.getMessage}")
      }
    }

    Await.result(system.whenTerminated, Duration.Inf)
  }

  private def postCommand(url: String): Unit = {
    import requests._
    try {
      val resp = post(url)
      println(s"[AI] Sent command, response: ${resp.statusCode}")
    } catch {
      case ex: Exception =>
        println(s"[AI] HTTP command failed: ${ex.getMessage}")
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
}
