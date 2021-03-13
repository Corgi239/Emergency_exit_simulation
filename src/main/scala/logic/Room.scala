package logic

class Room(val people: Vector[PersonBody]) {

  def coordinateList = people.map( _.location )

  def step(timePassed: Double) = {
    people.foreach( _.updateVelocity() )
    people.foreach( _.move(timePassed) )
  }

}