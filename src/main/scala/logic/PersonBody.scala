package logic

class PersonBody(var location: Vector2d, room: Room) {

  private var brain: Option[PersonBrain] = None
  private var currentVelocity: Vector2d = Vector2d(0, 0)

  def getExitMiddle = room.exitMiddle
  def getNeighbors = room.neighbors(this.location, PersonBody.searchRadius)

  def giveBrain(brain: PersonBrain) = this.brain = Some(brain)

  def updateVelocity(timePassed: Double) = {
    brain match {
      case Some(brain) =>
        currentVelocity = (currentVelocity + ((brain.targetVelocity() - currentVelocity).capMagnitude(PersonBody.MAX_ACC) * timePassed)).capMagnitude(PersonBody.MAX_SPD)
      case None =>
    }
  }

  def move(timePassed: Double) = location += currentVelocity * timePassed

  def facing = math.atan2(currentVelocity.x, currentVelocity.y)

}

object PersonBody {
  private val MAX_ACC: Double = 0.0001
  private val MAX_SPD: Double = 0.05
  private val searchRadius = 25.0
}