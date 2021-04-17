package UI

import logic._

import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}
import scala.collection.mutable
import scala.swing._
import scala.swing.event.{ButtonClicked, ValueChanged}


object EmergencyExitSimulation extends SimpleSwingApplication{

  private val roomWidth = 800
  private val roomHeight = 600
  private val margin = 10
  private val headerBarHeight = 30
  private val buttonsBarHeight = 30
  private val adjusterWidth = 350

  private val timeDelta = 30
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

  private val testCoords = (((620 to 710 by 30)).map( _.toDouble ).flatMap( i => (((30 to 500 by 30)).map( _.toDouble ).map( j => (i, j) )))).toVector

  private var room = Room(testCoords, roomWidth, roomHeight)
  room.people.foreach( b => b.giveBrain(new SimpleExitBrain(b)) )

  private def resetSimulation() = {
    val roomSpeed = room.roomMaxSpeed
    val roomSearchRadius = room.roomSearchRadius
    this.room = Room(testCoords, roomWidth, roomHeight)
    room.people.foreach( b => b.giveBrain(new SimpleExitBrain(b)) )
    room.setMaxSpeed(roomSpeed)
    room.setSearchRadius(roomSearchRadius)
    println("Reset. Speed: " + roomSpeed + " Search Radius: " + roomSearchRadius)
  }

  def top = new MainFrame {
    title = "Emergency Exit Simulation"
    contents = new BorderPanel {
      add(resetButton, BorderPanel.Position.South)
      add(simulationPanel, BorderPanel.Position.Center)
      add(parameterAdjustment, BorderPanel.Position.East)
    }
    size = new Dimension(roomWidth + margin * 2 + adjusterWidth, roomHeight + margin * 2 + headerBarHeight + buttonsBarHeight)
  }

  val resetButton = new Button("Reset simulation")
  val speedSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 20
        value = 10
        majorTickSpacing = 2
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("slow")
        labelTable += (max - min + 1) / 2  -> new Label("meduim")
        labelTable += max        -> new Label("fast")

        labels = labelTable
        paintLabels = true
  }
  val searchRadiusSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 5
        max   = 50
        value = 25
        majorTickSpacing = 5
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("5")
        labelTable += max        -> new Label("50")

        labels = labelTable
        paintLabels = true
  }
  val parameterAdjustment = new BoxPanel(Orientation.Vertical) {
    val speedAdjustment = new FlowPanel(new Label("Maximum speed: "), speedSlider)
    val searchRadiusAdjustment = new FlowPanel(new Label("Search radius: "), searchRadiusSlider)
    contents += speedAdjustment
    contents += searchRadiusAdjustment
  }

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
  this.listenTo(resetButton)
  this.listenTo(speedSlider)
  this.listenTo(searchRadiusSlider)
  this.reactions += {
    case clickEvent: ButtonClicked =>
      val clickedButton = clickEvent.source
      clickedButton match {
        case resetButton =>
          updatingTimer.stop()
          this.resetSimulation()
          updatingTimer.start()
      }
    case valueChange: ValueChanged =>
      val slider = valueChange.source.asInstanceOf[Slider]
      valueChange.source match {

        case s if s == speedSlider =>
          if (!slider.adjusting) {
            room.setMaxSpeed(slider.value.toDouble * 0.005)
          }


        case s if s == searchRadiusSlider =>
          if (!slider.adjusting) {
             room.setSearchRadius(slider.value.toDouble)
          }
      }

  }
  updatingTimer.start()
  drawingTimer.start()

  private val personDiameter = 10.0
  private val boidShapeX: Array[Int] = Array(0, -personDiameter * math.sqrt(3) / 4, 0, personDiameter * math.sqrt(3) / 4).map ( _.toInt )
  private val boidShapeY: Array[Int] = Array(0, personDiameter / 4, -personDiameter, personDiameter / 4).map ( _.toInt )
  private def drawPerson(g: Graphics2D, person: PersonBody) = {
    val coords = person.location.coordinates
    val theta = person.facing + Math.PI / 2
    val oldTransform = g.getTransform
    g.translate(coords._1, coords._2)
    g.rotate(theta)
    val brightness = (0.5 * Math.max(0, Math.min(person.gasRatio, 1))).toFloat
    g.setColor(new Color(brightness, brightness, brightness))
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
      room.people.foreach( drawPerson(g, _) )
      g.translate(-margin, -margin)
    }
  }
}
