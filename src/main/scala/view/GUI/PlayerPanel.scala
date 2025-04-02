package view.GUI

import model.modelComponent.PlayerState.{Betting, Idle, Playing, Standing}
import model.modelComponent.{Card, Hand, Player}
import view.GUI.CardPanel

import java.awt.Color
import java.net.URL
import javax.swing.{BorderFactory, ImageIcon}
import scala.swing.*

class PlayerPanel(player: Player, active: Boolean) extends BoxPanel(Orientation.Vertical) {
  preferredSize = if(player.hand.cards.nonEmpty) new Dimension(235, 250) else new Dimension(235, 150)
  minimumSize = new Dimension(235, 250)
  maximumSize = new Dimension(235, 250)

  private val poolTableGreen = new Color(0x0e5932)
  background = poolTableGreen

  private val cards: Seq[Card] = player.hand.cards

  private val border_color = if(active) java.awt.Color.WHITE else java.awt.Color.BLACK
  private val thickBorder = if(active) BorderFactory.createLineBorder(border_color, 2) else BorderFactory.createLineBorder(border_color, 1)
  private val titledBorder = BorderFactory.createTitledBorder(thickBorder, s"\t\t\t${player.name}\t\t\t")

  // Set a larger font for the title
  titledBorder.setTitleFont(new Font("Arial", Font.Bold.id, 18))
  if(active) titledBorder.setTitleColor(Color.WHITE) else titledBorder.setTitleColor(Color.BLACK)

  border = titledBorder

  private val cards_panel: FlowPanel = new FlowPanel() {
    background = poolTableGreen
    preferredSize = new Dimension(230, 50)

    val numCards: Int = cards.length
    var scale: Double = 0

    numCards match
      case 2 => scale = 0.3
      case 3 => scale = 0.25
      case 4 => scale = 0.2
      case 5 => scale = 0.16
      case _ => scale = 0.14


    contents += Swing.HStrut(5)
    for (card <- cards) {
      contents += new CardPanel(card, scale)
    }
    contents += Swing.HStrut(5)
  }

  private val player_stat_panel: BoxPanel = new BoxPanel(Orientation.Vertical) {
    background = poolTableGreen
    //xLayoutAlignment = Alignment.Center.id

    val player_font = new Font("Arial", Font.Bold.id, 18)

    contents += new BoxPanel(Orientation.Horizontal) {
      background = poolTableGreen
      val imagePath: URL = getClass.getResource("/dollar_tiny.png")
      contents += new Label() {
        icon = new ImageIcon(imagePath)
      }

      contents += Swing.HStrut(15)

      val label = Label(s"$$ ${player.money}")
      label.font = player_font
      label.foreground = Color.WHITE

      contents += label
    }

    contents += Swing.VStrut(3)


    contents += new BoxPanel(Orientation.Horizontal) {
      background = poolTableGreen
      val imagePath: URL = getClass.getResource("/casino-chip.png")
      contents += new Label() {
        icon = new ImageIcon(imagePath)
      }

      contents += Swing.HStrut(15)

      val bet_label = Label(s"$$ ${player.bet}")
      bet_label.font = player_font
      bet_label.foreground = Color.WHITE

      contents += bet_label
    }

    contents += Swing.VStrut(10)

    contents += new BoxPanel(Orientation.Horizontal) {
      background = poolTableGreen
      val state_label: Label =
        if(player.state == Playing)
          if(player.hand.cards.nonEmpty) Label(s"Value: ${player.hand.getHandValue}") else Label()
        else if(player.state == Standing)
          Label(s"Standing on: ${player.hand.getHandValue}")
        else if(player.state == Betting || player.state == Idle )
          Label("")
        else
          Label(s"${player.state} ${player.hand.getHandValue}")
      state_label.font = player_font
      state_label.foreground = Color.WHITE

      contents += state_label
    }

    contents += Swing.VStrut(3)

  }

  contents += new BorderPanel {
    background = poolTableGreen
    if(player.hand.cards.nonEmpty) add(cards_panel, BorderPanel.Position.Center)
    add(player_stat_panel, BorderPanel.Position.South)
  }
}