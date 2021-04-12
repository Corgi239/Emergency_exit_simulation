package logic

import scala.collection.mutable

class Room(coords: Vector[(Double, Double)],val roomWidth: Double, val roomHeight: Double, val exitLocation: Vector2d, val exitLength: Double) {

  var people: mutable.Buffer[PersonBody] = coords.map(p => Vector2d(p._1, p._2) ).map( new PersonBody(_, this) ).toBuffer

  def coordinateList = people.map( _.location.coordinates )

  def exitMiddle = exitLocation + Vector2d(0.0, exitLength / 2)

  var roomMaxSpeed = 0.05
  def setMaxSpeed(updSpeed: Double) = {
    roomMaxSpeed = updSpeed
    people.foreach( _.setMaxSpeed(updSpeed) )
  }

  def step(timePassed: Double) = {
    people.foreach( _.updateVelocity(timePassed) )
    people.foreach( _.move(timePassed) )
    people = people.filter( _.location.x < roomWidth)
  }

  def neighbors(point: Vector2d, radius: Double): Vector[(PersonBody, Double)] = {
    (people zip people.map( _.location.distance(point) )).filter( _._2 <= radius ).filter( _._2 > 0).toVector
  }

  def getBoundaryNormal(point: Vector2d): Vector2d = {
    var xComponent = 0.0
    var yComponent = 0.0
    if (point.x <= 0) {
       xComponent = 1.0
    } else if (point.x >= roomWidth && (point.y <= exitLocation.y || point.y >= (exitLocation.y + exitLength))) {
      xComponent = -1.0
    }

    if (point.y <= 0) {
      yComponent = 1.0
    } else if (point.y >= roomHeight) {
      yComponent = -1.0
    }

    Vector2d(xComponent, yComponent).normalize()
  }

}

object Room {

  def apply(coords: Vector[(Double, Double)], roomWidth: Double, roomHeight: Double, exitLength: Double = 50.0): Room = {
    new Room(coords, roomWidth, roomHeight, Vector2d(roomWidth, roomHeight / 2), exitLength)
  }

  def TestRoom() = new Room(Vector[(Double, Double)](), 100, 100, Vector2d(100, 45), 10)

}