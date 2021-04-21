package logic

import scala.collection.mutable
import scala.collection.parallel.CollectionConverters._

class Room(coords: Vector[(Double, Double)], val config: RoomConfig) {

  var people: mutable.Buffer[PersonBody] = coords.map(p => Vector2d(p._1, p._2) ).map( new PersonBody(_, this) ).toBuffer

  def init() = {
    people.foreach( b => b.giveBrain(new SimpleExitBrain(b)) )
    setMaxSpeed(config.maxSpeed)
    setSearchRadius(config.searchRadius)
  }

  def coordinateList = people.map( _.location.coordinates )

  def exitMiddle = config.exitLocation + Vector2d(0.0, config.exitLength / 2)

  def setMaxSpeed(updSpeed: Double) = {
    config.maxSpeed = updSpeed
    people.foreach( _.setMaxSpeed(updSpeed) )
  }
  def setSearchRadius(updRadius: Double) = {
    config.searchRadius = updRadius
    people.foreach( _.setSearchRadius(updRadius) )
  }

  def setLogicParameters(params: Map[String, Double]) = {
    people.foreach( _.setLogicParameters(params) )
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
    } else if (point.x >= config.roomWidth && (point.y <= config.exitLocation.y || point.y >= (config.exitLocation.y + config.exitLength))) {
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

  def apply(coords: Vector[(Double, Double)], roomWidth: Double, roomHeight: Double, exitLength: Double = 30.0): Room = {
    new Room(coords, new RoomConfig(roomWidth, roomHeight, Vector2d(roomWidth, roomHeight / 2), exitLength))
  }

}

class RoomConfig(val roomWidth: Double,
                 val roomHeight: Double,
                 val exitLocation: Vector2d,
                 val exitLength: Double,
                 var maxSpeed: Double = 0.05,
                 var searchRadius: Double = 25.0,
                 var seekingWeight: Double = 20.0,
                 var separationWeight: Double = 120.0,
                 var containmentWeight: Double = 30.0) {
  override def toString: String = "Current room settings:\n" + s"Dimentions: ${roomWidth}x${roomHeight}\nExit of length $exitLength at $exitLocation\nMax speed: $maxSpeed\nSearch radius: $searchRadius"
}