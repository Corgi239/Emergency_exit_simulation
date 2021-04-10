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

}

object Room {

  def apply(coords: Vector[(Double, Double)], roomWidth: Double, roomHeight: Double, exitLength: Double = 50.0): Room = {
    new Room(coords, roomWidth, roomHeight, Vector2d(roomWidth, roomHeight / 2), exitLength)
  }

}