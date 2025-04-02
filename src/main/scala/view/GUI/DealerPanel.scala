package view.GUI

import model.modelComponent.DealerState.{Bust, Dealing, Idle, Standing}
import model.modelComponent.{Card, Dealer}
import view.GUI.CardPanel

import java.awt.Color
import java.net.URL
import javax.swing.{BorderFactory, ImageIcon}
import scala.swing.*

class DealerPanel(dealer: Dealer) extends BoxPanel(Orientation.Vertical) {
  preferredSize = new Dimension(235, 280)
  minimumSize = new Dimension(235, 280)
  maximumSize = new Dimension(235, 280)

  private val poolTableGreen = new Color(0x0e5932)
  background = poolTableGreen

  val player_font = new Font("Arial", Font.Bold.id, 18)

  private val cards: Seq[Card] = dealer.hand.cards
  private val blank_card = Card("blank", "blank")

  val imagePath: URL = getClass.getResource("/business.png")
  private val dealer_image: Label = new Label() {
    icon = new ImageIcon(imagePath)
  }

  private val cards_panel: FlowPanel = new FlowPanel() {
    background = poolTableGreen
    preferredSize = new Dimension(230, 100)
    minimumSize = new Dimension(235, 270)
    maximumSize = new Dimension(235, 270)

    val numCards: Int = cards.length
    var scale: Double = 0

    numCards match
      case 1 => scale = 0.3
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

    if (cards.length == 1) {
      contents += new CardPanel(blank_card, scale)
      contents += Swing.HStrut(5)
    }
  }

  private val dealer_stat_panel: BoxPanel = new BoxPanel(Orientation.Vertical) {
    background = poolTableGreen

    contents += Swing.HStrut(10)

    contents += new BoxPanel(Orientation.Horizontal) {
      background = poolTableGreen
      val state_label: Label =
        if (dealer.hand.hasBlackjack)
          Label(s"BLACKJACK - ${dealer.hand.getHandValue}")
        else if (dealer.state == Bust)
          Label(s"${dealer.state} - ${dealer.hand.getHandValue}")
        else if(dealer.state != Bust)
          Label(s"Value: ${dealer.hand.getHandValue}")
        else
          Label()
      state_label.font = player_font
      state_label.foreground = Color.WHITE

      contents += state_label
    }
    contents += Swing.HStrut(10)
  }

  contents += new BorderPanel {
    background = poolTableGreen
    add(dealer_image, BorderPanel.Position.North)
    if (dealer.hand.cards.nonEmpty) add(cards_panel, BorderPanel.Position.Center)
    if(dealer.hand.cards.nonEmpty) add(dealer_stat_panel, BorderPanel.Position.South)
  }

  border = Swing.EmptyBorder(10)
}
