package UI

import logic._

import java.awt.Color
import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import scala.collection.mutable
import scala.swing.Swing.EmptyBorder
import scala.swing._
import scala.swing.event.{ButtonClicked, ValueChanged}
import scala.util.Random


object EmergencyExitSimulation extends SimpleSwingApplication{

  private val timeDelta = 30
  private val fps = 60
  private val defaultRoomWidth = 800.0
  private val defaultRoomHeight = 600.0

  private var room = Room(Vector[(Double, Double)](), defaultRoomWidth, defaultRoomHeight)
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

  private def resetSimulationFromFile(filepath: String) = {
    val config = RoomConfig.createFromFile(filepath)
    resetSliderValues(config)
    this.room = new Room(config)
    room.init()
  }

  def top = new MainFrame {
    title = "Emergency Exit Simulation"
    contents = new BorderPanel {
      add(simulationControls, BorderPanel.Position.South)
      add(simulationPanel, BorderPanel.Position.Center)
      add(parameterAdjustment, BorderPanel.Position.East)

      border = EmptyBorder(10)
    }

    size = new Dimension(Math.max(parameterAdjustment.size.width + simulationPanel.size.width + 40, simulationControls.size.width + 30),
                         simulationControls.size.height + Math.max(parameterAdjustment.size.height + 50, simulationPanel.size.height + 70))
  }

  val restartButton = new Button("Restart simulation") {
    tooltip = "Start the simulation over while retaining any settings"
  }
  val resetSimulationWithRandomButton = new Button("Start simulation with random distribution of people") {
    tooltip = "Reset the simulation using a randomly generated distribution of people across the room"
  }
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
  val resetFromFileButton = new Button("Start simulation from configuration file") {
    tooltip = "Start the simulation using the settings specified in a configuration file"
  }
  val speedSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 20
        value = (room.config.maxSpeed * 200).toInt
        majorTickSpacing = 2
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("0.00")
        labelTable += (max - min + 1) / 2  -> new Label("0.05")
        labelTable += max        -> new Label("0.10")

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
        labelTable += min    -> new Label("0.0000")
        labelTable += (max - min + 1) / 2  -> new Label("0.0001")
        labelTable += max        -> new Label("0.0002")

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
        labelTable += min        -> new Label("5")
        labelTable += 25 -> new Label("25")
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
        labelTable += 50     -> new Label("50")
        labelTable += max    -> new Label("100")

        labels = labelTable
        paintLabels = true
  }
  val separationComponentSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 100
        value = room.config.separationWeight.toInt
        majorTickSpacing = 10
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min    -> new Label("0")
        labelTable += 50     -> new Label("50")
        labelTable += max    -> new Label("100")

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
        labelTable += 50     -> new Label("50")
        labelTable += max    -> new Label("100")

        labels = labelTable
        paintLabels = true
  }
  val exitSizeSlider = new Slider {
        orientation = Orientation.Horizontal
        min   = 0
        max   = 50
        value = (room.config.exitSize * 100).toInt
        majorTickSpacing = 5
        paintTicks = true

        val labelTable = mutable.HashMap[Int, Label]()
        labelTable += min               -> new Label("0%")
        labelTable += (min + max)/2     -> new Label("25%")
        labelTable += max               -> new Label("50%")

        labels = labelTable
        paintLabels = true
  }
  val parameterAdjustment = new BoxPanel(Orientation.Vertical) {
    val speedAdjustment = new FlowPanel(new Label("Maximum speed: "){tooltip = "Adjust the maximum speed a person can achieve"}, speedSlider)
    val accelerationAdjustment = new FlowPanel(new Label("Maximum acceleration: "){tooltip = "Adjust how quickly a person can change its velocity"}, accelerationSlider)
    val searchRadiusAdjustment = new FlowPanel(new Label("Search radius: "){tooltip = "Adjust how far a person will look when determining its neighbors"}, searchRadiusSlider)
    val seekingComponentAdjustment = new FlowPanel(new Label("Seeking component weight: "){tooltip = "Adjust the extent to which the seeking behaviour influences a person's velocity"}, seekingComponentSlider)
    val separationComponentAdjustment = new FlowPanel(new Label("Separation component weight: "){tooltip = "Adjust the extent to which the separation behaviour influences a person's velocity"}, separationComponentSlider)
    val containmentComponentAdjustment = new FlowPanel(new Label("Containment component weight: "){tooltip = "Adjust the extent to which the containment behaviour influences a person's velocity"}, containmentComponentSlider)
    val exitSizeAdjustment = new FlowPanel(new Label("Relative exit size: "){tooltip = "Adjust size of the exit relative to the dimentions of the room"}, exitSizeSlider)
    contents += speedAdjustment
    contents += accelerationAdjustment
    contents += searchRadiusAdjustment
    contents += seekingComponentAdjustment
    contents += separationComponentAdjustment
    contents += containmentComponentAdjustment
    contents += exitSizeAdjustment

  }
  val simulationControls = new BoxPanel(Orientation.Vertical) {
    val densityAdjustment = new FlowPanel(new Label("Density: "){tooltip = "Adjust how densly the room will be populated when the reset button is pressed"}, densitySlider)
    val resetWithRandomControls = new FlowPanel(resetSimulationWithRandomButton, densityAdjustment)
    contents += restartButton
    contents += resetWithRandomControls
    contents += resetFromFileButton
  }

  def resetSliderValues(config: RoomConfig) = {
    speedSlider.value = (config.maxSpeed * 200).toInt
    accelerationSlider.value = (config.maxAcc * 50000).toInt
    searchRadiusSlider.value = config.searchRadius.toInt
    seekingComponentSlider.value = config.seekingWeight.toInt
    separationComponentSlider.value = config.separationWeight.toInt
    containmentComponentSlider.value = config.containmentWeight.toInt
    exitSizeSlider.value = (config.exitSize * 100).toInt
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
  this.listenTo(resetFromFileButton)
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
          this.resetSimulationWithRandomStart(densitySlider.value.toDouble / 1000, Random.nextInt())
          updatingTimer.start()
        case b if b == resetFromFileButton =>
          updatingTimer.stop()
          val chooser = new FileChooser(new File("."))
          chooser.showOpenDialog(null)
          val filepath = chooser.selectedFile.getAbsolutePath
          this.resetSimulationFromFile(filepath)
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
      g.setColor(Color.black)
      g.drawRect(0, 0, room.config.roomWidth.toInt, room.config.roomHeight.toInt)
      g.setColor(Color.red)
      g.drawLine(room.config.exitLocation.x.toInt, room.config.exitLocation.y.toInt, room.config.exitLocation.x.toInt, (room.config.exitLocation.y + room.config.exitSize * room.config.roomHeight).toInt)
      room.people.foreach( drawPerson(g, _) )
    }

    override def size: Dimension = new Dimension(room.config.roomWidth.toInt, room.config.roomHeight.toInt)
  }
}
