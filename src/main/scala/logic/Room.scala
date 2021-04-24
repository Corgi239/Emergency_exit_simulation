package logic

import IO._
import scala.collection.mutable
import scala.collection.parallel.CollectionConverters._
import scala.util.Random

class Room(val config: RoomConfig) {

  var people: mutable.Buffer[PersonBody] = config.startingCoords.map(p => Vector2d(p._1, p._2) ).map( new PersonBody(_, this) ).toBuffer

  def init() = {
    people.foreach( b => b.giveBrain(new SimpleExitBrain(b)) )
    setMaxSpeed(config.maxSpeed)
    setSearchRadius(config.searchRadius)
    setSearchRadius(config.searchRadius)
    setLogicParameters(Map("seekingWeight" -> config.seekingWeight, "separationWeight" -> config.separationWeight, "containmentWeight" -> config.separationWeight))
  }

  def coordinateList = people.map( _.location.coordinates )

  def exitMiddle = config.exitLocation + Vector2d(0.0, config.exitSize * config.roomHeight / 2)

  def setMaxSpeed(updSpeed: Double) = {
    config.maxSpeed = updSpeed
    people.foreach( _.setMaxSpeed(updSpeed) )
  }
  def setMaxAcceleration(updAcc: Double) = {
    config.maxAcc = updAcc
    people.foreach( _.setMaxAcceleration(updAcc) )
  }
  def setSearchRadius(updRadius: Double) = {
    config.searchRadius = updRadius
    people.foreach( _.setSearchRadius(updRadius) )
  }

  def setLogicParameters(params: Map[String, Double]) = {
    people.foreach( _.setLogicParameters(params) )
  }

  def setExitSize(updSize: Double) = {
    config.exitSize = updSize
  }

  def setExitLocation(updLocation: Vector2d) = {
    config.exitLocation = updLocation
  }

  def step(timePassed: Double) = {
    people.par.foreach( _.updateVelocity(timePassed) )
    people.par.foreach( _.move(timePassed) )
    people = people.filter( _.location.x < config.roomWidth)
  }

  def neighbors(point: Vector2d, radius: Double): Vector[(PersonBody, Double)] = {
    (people zip people.par.map( _.location.distance(point) )).filter( _._2 <= radius ).filter( _._2 > 0).toVector
  }

  def getBoundaryNormal(point: Vector2d): Vector2d = {
    var xComponent = 0.0
    var yComponent = 0.0
    if (point.x <= 0) {
       xComponent = 1.0
    } else if (point.x >= config.roomWidth && (point.y <= config.exitLocation.y || point.y >= (config.exitLocation.y + config.exitSize * config.roomHeight))) {
      xComponent = -1.0
    }

    if (point.y <= 0) {
      yComponent = 1.0
    } else if (point.y >= config.roomHeight) {
      yComponent = -1.0
    }

    Vector2d(xComponent, yComponent).normalize()
  }

}

object Room {

  def apply(coords: Vector[(Double, Double)], roomWidth: Double, roomHeight: Double): Room = {
    new Room(RoomConfig(coords, roomWidth, roomHeight))
  }

}

class RoomConfig(var startingCoords: Vector[(Double, Double)],
                 val roomWidth: Double,
                 val roomHeight: Double,
                 var exitSize: Double,
                 var exitLocation: Vector2d,
                 var maxSpeed: Double,
                 var maxAcc: Double,
                 var searchRadius: Double,
                 var seekingWeight: Double,
                 var separationWeight: Double,
                 var containmentWeight: Double) {
  override def toString: String = "Current room settings:\n" + s"Dimentions: ${roomWidth}x${roomHeight}\nExit of length $exitSize at $exitLocation\nMax speed: $maxSpeed\nSearch radius: $searchRadius"

  def generateRandomStartingCoords(density: Double, seed: Int) = {
    val r = new Random(seed)
    val numberOfPeople = (roomHeight * roomWidth * density / 100).toInt
    val newCoords = for (i <- 1 to numberOfPeople) yield (r.nextDouble() * roomWidth, r.nextDouble() * roomHeight)
    startingCoords = newCoords.toVector
  }
}

object RoomConfig {
  def apply(startingCoords: Vector[(Double, Double)],
            roomWidth: Double,
            roomHeight: Double,
            exitSize: Double = 0.05,
            exitLocation: Vector2d = Vector2d(0, 0),
            maxSpeed: Double = 0.05,
            maxAcc: Double = 0.0001,
            searchRadius: Double = 25.0,
            seekingWeight: Double = 20.0,
            separationWeight: Double = 120.0,
            containmentWeight: Double = 30.0) = {
    new RoomConfig(startingCoords, roomWidth, roomHeight, exitSize, if (exitLocation.coordinates == (0, 0)) Vector2d(roomWidth, roomHeight * (0.5 - exitSize / 2)) else exitLocation, maxSpeed, maxAcc, searchRadius, seekingWeight, separationWeight, containmentWeight)
  }

  def createFromFile(filepath: String): RoomConfig = {
    new ConfigBuilder(filepath).build()
  }
}
