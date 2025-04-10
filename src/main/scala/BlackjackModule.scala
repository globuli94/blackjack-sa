import com.google.inject.AbstractModule
import controller.ControllerInterface
import controller.controllerComponent.Controller
import net.codingwell.scalaguice.ScalaModule
import model.GameInterface
import model.modelComponent.Game
import util.FileIOInterface
import util.fileIOComponent.JSON.FileIOJSON
import util.fileIOComponent.XML.FileIOXML

class BlackjackModule extends AbstractModule with ScalaModule {
  override def configure(): Unit =
    val game = Game()
    bind[GameInterface].toInstance(game)
    bind[ControllerInterface].to[Controller]

    val useJson = System.getProperty("fileio.json", "false").toBoolean
    if (useJson) {
      bind[FileIOInterface].to[FileIOJSON]
    } else {
      bind[FileIOInterface].to[FileIOXML]
    }
}