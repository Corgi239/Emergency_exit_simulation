package logic

class Room(coords: Vector[(Double, Double)], val exitLocation: Vector2d, val exitLength: Double) {

  val people = coords.map( p => Vector2d(p._1, p._2) ).map( new PersonBody(_, this) )

  def coordinateList = people.map( _.location.coordinates )

  def exitMiddle = exitLocation + Vector2d(0.0, exitLength / 2)

  def step(timePassed: Double) = {
    people.foreach( _.updateVelocity(timePassed) )
    people.foreach( _.move(timePassed) )
  }

}

object Room {

  def apply(coords: Vector[(Double, Double)], exitLocation: Vector2d = Vector2d(810, 310), exitLength: Double = 50.0): Room = {
    new Room(coords, exitLocation, exitLength)
  }

}