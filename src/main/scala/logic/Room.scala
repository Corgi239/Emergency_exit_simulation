package logic

import scala.collection.mutable
import scala.collection.parallel.CollectionConverters._
import scala.util.Random

class Room(val config: RoomConfig) {

  var people: mutable.Buffer[PersonBody] = config.startingCoords.map(p => Vector2d(p._1, p._2) ).map( new PersonBody(_, this) ).toBuffer

  def init() = {
    people.foreach( b => b.giveBrain(new SimpleExitBrain(b)) )
    setMaxSpeed(config.maxSpeed)
    setSearchRadius(config.searchRadius)
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

  def apply(coords: Vector[(Double, Double)], roomWidth: Double, roomHeight: Double, exitSize: Double = 0.05): Room = {
    new Room(coords, new RoomConfig(roomWidth, roomHeight, Vector2d(roomWidth, roomHeight * (0.5 - exitSize / 2)), exitSize))
  }

}

class RoomConfig(val roomWidth: Double,
                 val roomHeight: Double,
                 val exitLocation: Vector2d,
                 var exitSize: Double = 0.05,
                 var maxSpeed: Double = 0.05,
                 var searchRadius: Double = 25.0,
                 var seekingWeight: Double = 20.0,
                 var separationWeight: Double = 120.0,
                 var containmentWeight: Double = 30.0) {
  override def toString: String = "Current room settings:\n" + s"Dimentions: ${roomWidth}x${roomHeight}\nExit of length $exitSize at $exitLocation\nMax speed: $maxSpeed\nSearch radius: $searchRadius"
}
