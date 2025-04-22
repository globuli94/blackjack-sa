import com.google.inject.{Guice, Injector}
import controller.ControllerInterface

import scala.io.StdIn.readLine
import view.TUI.TUI
import view.GUI.GUI
import controller.controllerComponent.Server


object Main {
  private val injector: Injector = Guice.createInjector(new BlackjackModule)
  private val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  private val tui: TUI = TUI(controller)
  private val gui: GUI = GUI(controller)

  print(controller.toString)

  def main(args: Array[String]): Unit = {

    new Thread(() => Server.main(args)).start()
    println("Hello")

    var input: String = ""

    while(input != "exit") {
      input = readLine();
      tui.getInputAndPrintLoop(input)
    }
  }
}