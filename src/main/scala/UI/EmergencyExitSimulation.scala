package UI

import logic._

import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}
import scala.swing._


object EmergencyExitSimulation extends SimpleSwingApplication{

  private val timeDelta = 10

  private val testCoords = Vector(
    (20.0, 20.0),
    (40.0, 80.0),
    (50.0, 120.0),
    (350.0, 200.0)
  )

  private val room = Room(testCoords)
  room.people.foreach( b => b.giveBrain(new TestBrain(b)) )

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

  private val personDiameter = 20
  private def drawPerson(g: Graphics2D, coords: (Double, Double)) = {
    def getXcoords(x: Double): Array[Int] = Array(x, x - personDiameter * math.sqrt(3) / 4, x, x + personDiameter * math.sqrt(3) / 4).map ( _.toInt )
    def getYcoords(y: Double): Array[Int] = Array(y, y + personDiameter / 4, y - personDiameter, y + personDiameter / 4).map ( _.toInt )
    g.fillPolygon(getXcoords(coords._1), getYcoords(coords._2), 4)
    //g.fillOval(coords._1.toInt, coords._2.toInt, personDiameter, personDiameter)

  }

  private val simulationPanel = new Panel {
    override def paintComponent(g: Graphics2D) = {
      g.setColor(Color.darkGray)
      room.coordinateList.foreach( drawPerson(g, _) )
    }
  }
}
