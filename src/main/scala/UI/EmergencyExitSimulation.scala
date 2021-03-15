package UI

import logic._

import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}
import scala.swing._


object EmergencyExitSimulation extends SimpleSwingApplication{

  private val timeDelta = 6

  private val testCoords = Vector(
    (20.0, 20.0),
    (40.0, 80.0),
    (50.0, 120.0),
    (350.0, 200.0)
  )

  private val room = Room(testCoords)

  def top = new MainFrame {
    title = "Emergency Exit Simulation"
    contents = simulationPanel
    size = new Dimension(800, 600)

    def listener = new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        room.step(timeDelta)
        simulationPanel.repaint()
      }
    }

    val mainTimer = new javax.swing.Timer(timeDelta, listener)
    mainTimer.start()

  }

  private def drawPerson(g: Graphics2D, coords: (Double, Double)) = {
    val personDiameter = 20
    g.fillOval(coords._1.toInt, coords._2.toInt, personDiameter, personDiameter)
  }

  private val simulationPanel = new Panel {
    override def paintComponent(g: Graphics2D) = {
      g.setColor(Color.darkGray)
      room.coordinateList.foreach( drawPerson(g, _) )
    }
  }
}
