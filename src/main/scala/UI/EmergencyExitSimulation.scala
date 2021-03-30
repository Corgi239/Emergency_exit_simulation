package UI

import logic._

import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}
import scala.swing._


object EmergencyExitSimulation extends SimpleSwingApplication{

  private val roomWidth = 800
  private val roomHeight = 600
  private val margin = 10
  private val headerBarHeight = 30

  private val timeDelta = 10
  private val fps = 60

/*
  private val testCoords = Vector(
    (20.0, 20.0),
    (22.0, 22.0),
    (19.0, 18.0),
    (17.0, 24.0),
    (15.0, 35.0),
    (24.0, 14.0),
    (30.0, 20.0),
    (40.0, 20.0),
    (40.0, 80.0),
    (50.0, 120.0),
    (350.0, 200.0),
    (700.0, 100.0),
    (550.0, 380.0)
  )


 */

  private val testCoords = ((30 to 750 by 30).map( _.toDouble ).flatMap( i => ((30 to 350 by 30).map( _.toDouble ).map( j => (i, j) )))).toVector

  private val room = Room(testCoords, roomWidth, roomHeight)
  room.people.foreach( b => b.giveBrain(new SimpleExitBrain(b)) )

  def top = new MainFrame {
    title = "Emergency Exit Simulation"
    contents = simulationPanel
    size = new Dimension(roomWidth + margin * 2, roomHeight + margin * 2 + headerBarHeight)

    def redrawer = new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        simulationPanel.repaint()
      }
    }

    def updater = new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        room.step(timeDelta)
      }
    }

    val drawingTimer = new javax.swing.Timer(1000 / fps, redrawer)
    val updatingTimer = new javax.swing.Timer(timeDelta, updater)
    updatingTimer.start()
    drawingTimer.start()

  }

  private val personDiameter = 10.0
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
      g.translate(margin, margin)
      g.setColor(Color.black)
      g.drawRect(0, 0, roomWidth, roomHeight)
      g.setColor(Color.red)
      g.drawLine(room.exitLocation.coordinates._1.toInt, room.exitLocation.coordinates._2.toInt, room.exitLocation.coordinates._1.toInt, (room.exitLocation.coordinates._2 + room.exitLength).toInt)
      g.setColor(Color.darkGray)
      room.people.foreach( drawPerson(g, _) )
      g.translate(-margin, -margin)
    }
  }
}
