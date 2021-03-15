package logic

class Room(val people: Vector[PersonBody]) {

  def coordinateList = people.map( _.location.coordinates )

  def step(timePassed: Double) = {
    people.foreach( _.updateVelocity(timePassed) )
    people.foreach( _.move(timePassed) )
  }

}

object Room {

  def apply(coords: Vector[(Double, Double)]): Room = {
    val people = coords.map( p => Vector2d(p._1, p._2) ).map( new PersonBody(_) )
    new Room(people)
  }

}