package main

import com.google.inject.{Guice, Injector}
import controller.ControllerInterface
import controllerServer.ControllerServer
import main.BlackjackModule
import view.GUI.GUI
import view.TUI.TUI
import scala.io.StdIn.readLine


object Main {
  private val injector: Injector = Guice.createInjector(new BlackjackModule)
  private val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  private val tui: TUI = TUI(controller)
  private val gui: GUI = GUI(controller)

  print(controller.toString)

  def main(args: Array[String]): Unit = {

    new Thread(() => ControllerServer.main(args, injector)).start()
    println("Hello")

    var input: String = ""

    while(input != "exit") {
      input = readLine();
      tui.getInputAndPrintLoop(input)
    }
  }
}