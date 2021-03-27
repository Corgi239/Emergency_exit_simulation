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
    (350.0, 200.0),
    (700.0, 100.0),
    (550.0, 380.0)
  )

  private val room = Room(testCoords)
  room.people.foreach( b => b.giveBrain(new SimpleExitBrain(b)) )

  def top = new MainFrame {
    title = "Emergency Exit Simulation"
    contents = simulationPanel
    size = new Dimension(820, 620)

    def listener = new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        room.step(timeDelta)
        simulationPanel.repaint()
      }
    }

    val mainTimer = new javax.swing.Timer(timeDelta, listener)
    mainTimer.start()

  }

  private val personDiameter = 20.0
  private val boidShapeX: Array[Int] = Array(0, -personDiameter * math.sqrt(3) / 4, 0, personDiameter * math.sqrt(3) / 4).map ( _.toInt )
  private val boidShapeY: Array[Int] = Array(0, personDiameter / 4, -personDiameter, personDiameter / 4).map ( _.toInt )
  private def drawPerson(g: Graphics2D, person: PersonBody) = {
    val coords = person.location.coordinates
    val theta = math.Pi - person.facing
    val oldTransform = g.getTransform
    g.translate(coords._1, coords._2)
    g.rotate(theta)
    g.fillPolygon(boidShapeX, boidShapeY, 4)
    g.setTransform(oldTransform)
  }

  private val simulationPanel = new Panel {
    override def paintComponent(g: Graphics2D) = {
      g.setColor(Color.red)
      g.drawLine(room.exitLocation.coordinates._1.toInt, room.exitLocation.coordinates._2.toInt, room.exitLocation.coordinates._1.toInt, (room.exitLocation.coordinates._2 + room.exitLength).toInt)
      g.setColor(Color.darkGray)
      room.people.foreach( drawPerson(g, _) )
    }
  }
}
