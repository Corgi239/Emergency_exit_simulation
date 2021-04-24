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
  private val buttonsBarHeight = 120
  private val adjusterWidth = 420

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

  private val defaultCoords = (((120 to 710 by 30)).map( _.toDouble ).flatMap( i => (((30 to 500 by 30)).map( _.toDouble ).map( j => (i, j) )))).toVector

  private var room = new Room(RoomConfig.createFromFile("src/test/testConfigFile"))
  room.people.foreach( b => b.giveBrain(new SimpleExitBrain(b)) )
  restartSimulation()

  private def restartSimulation() = {
    val config = room.config
    this.room = new Room(config)
    room.init()
  }

  private def resetSimulationWithRandomStart(density: Double, seed: Int = 42) = {
     val config = room.config
    config.generateRandomStartingCoords(density, seed)
    this.room = new Room(config)
    room.init()
  }

  def top = new MainFrame {
    title = "Emergency Exit Simulation"
    contents = new BorderPanel {
      add(simulationControls, BorderPanel.Position.South)
      add(simulationPanel, BorderPanel.Position.Center)
      add(parameterAdjustment, BorderPanel.Position.East)
    }

    minimumSize = new Dimension(simulationControls.size.width + simulationPanel.size.width,
                                simulationControls.size.height + Math.max(parameterAdjustment.size.height + 20, simulationPanel.size.height + 50))
  }

  val restartButton = new Button("Restart simulation")
  val resetSimulationWithRandomButton = new Button("Reset simulation with random distribution of people")
  val densitySlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 100
        value = 50
        majorTickSpacing = 20
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("sparse")
        labelTable += (max - min + 1) / 2  -> new Label("meduim")
        labelTable += max        -> new Label("dense")

        labels = labelTable
        paintLabels = true
  }
  val speedSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 20
        value = (room.config.maxSpeed * 200).toInt
        majorTickSpacing = 2
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("slow")
        labelTable += (max - min + 1) / 2  -> new Label("meduim")
        labelTable += max        -> new Label("fast")

        labels = labelTable
        paintLabels = true
  }
  val accelerationSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 10
        value = (room.config.maxAcc * 50000).toInt
        majorTickSpacing = 1
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("low")
        labelTable += (max - min + 1) / 2  -> new Label("meduim")
        labelTable += max        -> new Label("high")

        labels = labelTable
        paintLabels = true
  }
  val searchRadiusSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 5
        max   = 50
        value = room.config.searchRadius.toInt
        majorTickSpacing = 5
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("5")
        labelTable += max        -> new Label("50")

        labels = labelTable
        paintLabels = true
  }
  val seekingComponentSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 100
        value = room.config.seekingWeight.toInt
        majorTickSpacing = 10
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("0")
        labelTable += 50     -> new Label("5")
        labelTable += max    -> new Label("10")

        labels = labelTable
        paintLabels = true
  }
  val separationComponentSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 200
        value = room.config.separationWeight.toInt
        majorTickSpacing = 20
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("0")
        labelTable += 100     -> new Label("5")
        labelTable += max    -> new Label("10")

        labels = labelTable
        paintLabels = true
  }
  val containmentComponentSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 100
        value = room.config.containmentWeight.toInt
        majorTickSpacing = 10
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("0")
        labelTable += 50     -> new Label("5")
        labelTable += max    -> new Label("10")

        labels = labelTable
        paintLabels = true
  }
  val exitSizeSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 5
        max   = 50
        value = (room.config.exitSize * 100).toInt
        majorTickSpacing = 5
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("5%")
        labelTable += 50     -> new Label("25%")
        labelTable += max    -> new Label("50%")

        labels = labelTable
        paintLabels = true
  }
  val parameterAdjustment = new BoxPanel(Orientation.Vertical) {
    val speedAdjustment = new FlowPanel(new Label("Maximum speed: "), speedSlider)
    val accelerationAdjustment = new FlowPanel(new Label("Maneuverability: "), accelerationSlider)
    val searchRadiusAdjustment = new FlowPanel(new Label("Search radius: "), searchRadiusSlider)
    val seekingComponentAdjustment = new FlowPanel(new Label("Seeking component weight: "), seekingComponentSlider)
    val separationComponentAdjustment = new FlowPanel(new Label("Separation component weight: "), separationComponentSlider)
    val containmentComponentAdjustment = new FlowPanel(new Label("Containment component weight: "), containmentComponentSlider)
    val exitSizeAdjustment = new FlowPanel(new Label("Exit size (as % of the right wall): "), exitSizeSlider)
    contents += speedAdjustment
    contents += accelerationAdjustment
    contents += searchRadiusAdjustment
    contents += seekingComponentAdjustment
    contents += separationComponentAdjustment
    contents += containmentComponentAdjustment
    contents += exitSizeAdjustment
  }
  val simulationControls = new BoxPanel(Orientation.Vertical) {
    val densityAdjustment = new FlowPanel(new Label("Density: "), densitySlider)
    val resetWithRandomControls = new FlowPanel(resetSimulationWithRandomButton, densityAdjustment)
    contents += restartButton
    contents += resetWithRandomControls
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
  this.listenTo(restartButton)
  this.listenTo(resetSimulationWithRandomButton)
  this.listenTo(speedSlider)
  this.listenTo(accelerationSlider)
  this.listenTo(searchRadiusSlider)
  this.listenTo(seekingComponentSlider)
  this.listenTo(separationComponentSlider)
  this.listenTo(containmentComponentSlider)
  this.listenTo(exitSizeSlider)
  this.reactions += {
    case clickEvent: ButtonClicked =>
      val clickedButton = clickEvent.source
      clickedButton match {
        case b if b == restartButton =>
          updatingTimer.stop()
          this.restartSimulation()
          updatingTimer.start()
        case b if b == resetSimulationWithRandomButton =>
          updatingTimer.stop()
          this.resetSimulationWithRandomStart(densitySlider.value.toDouble / 1000)
          updatingTimer.start()
      }
    case valueChange: ValueChanged =>
      val slider = valueChange.source.asInstanceOf[Slider]
      valueChange.source match {

        case s if s == speedSlider =>
          if (!slider.adjusting) {
            room.setMaxSpeed(slider.value.toDouble * 0.005)
          }
        case s if s == accelerationSlider =>
          if (!slider.adjusting) {
            room.setMaxAcceleration(slider.value.toDouble * 0.00002)
          }
        case s if s == searchRadiusSlider =>
          if (!slider.adjusting) {
             room.setSearchRadius(slider.value.toDouble)
          }
        case s if s == seekingComponentSlider =>
          if (!slider.adjusting) {
             room.setLogicParameters(Map("seekingWeight" -> slider.value.toDouble))
          }
        case s if s == separationComponentSlider =>
          if (!slider.adjusting) {
             room.setLogicParameters(Map("separationWeight" -> slider.value.toDouble))
          }
        case s if s == containmentComponentSlider =>
          if (!slider.adjusting) {
             room.setLogicParameters(Map("containmentWeight" -> slider.value.toDouble))
          }
        case s if s == exitSizeSlider =>
          if (!slider.adjusting) {
             room.setExitSize(slider.value.toDouble / 100)
             room.setExitLocation(Vector2d(room.config.roomWidth, room.config.roomHeight * (0.5 - room.config.exitSize / 2)))
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
      g.drawRect(0, 0, room.config.roomWidth.toInt, room.config.roomHeight.toInt)
      g.setColor(Color.red)
      g.drawLine(room.config.exitLocation.x.toInt, room.config.exitLocation.y.toInt, room.config.exitLocation.x.toInt, (room.config.exitLocation.y + room.config.exitSize * room.config.roomHeight).toInt)
      room.people.foreach( drawPerson(g, _) )
      g.translate(-margin, -margin)
    }

    override def size: Dimension = new Dimension(room.config.roomWidth.toInt, room.config.roomHeight.toInt)
  }
}
