package UI

import logic._

import java.awt.Color
import scala.swing._


object EmergencyExitSimulation extends SimpleSwingApplication{

  private val testCoords = Vector(
    (20.0, 20.0),
    (40.0, 80.0),
    (50.0, 120.0),
    (200.0, 350.0)
  )

  private val room = Room(testCoords)

  def top = new MainFrame {
    title = "Emergency Exit Simulation"
    contents = new SimulationPanel
    size = new Dimension(400, 400)
  }

  class SimulationPanel extends Panel {
    override def paintComponent(g: Graphics2D) = {
      g.setColor(Color.darkGray)
      room.coordinateList.foreach(c => g.fillOval(c._1.toInt, c._2.toInt, 10, 10))
    }
  }
}
