package validationComponent

import play.api.libs.json._
import scala.util.{Try, Success, Failure}

object Validator {
  def parseGameState(jsonString: String): Try[GameState] = Try {
    val jsValue = Json.parse(jsonString)
    jsValue.validate[GameState] match {
      case JsSuccess(gameState, _) => gameState
      case JsError(errors) =>
        throw new IllegalArgumentException("Invalid json structure")
    }
  }
}

