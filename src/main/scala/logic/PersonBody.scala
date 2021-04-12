package logic

class PersonBody(var location: Vector2d, room: Room) {

  private var brain: Option[PersonBrain] = None
  private var currentVelocity: Vector2d = Vector2d(0, 0)

  private var maxAcc: Double = 0.0001
  private var maxSpd: Double = 0.05
  private var searchRadius = 25.0
  private val containmentProbeDistance = 20.0

  def maxSpeed = maxSpd
  def setMaxSpeed(updMaxSpd: Double) = maxSpd = updMaxSpd

  def getExitMiddle = room.exitMiddle
  def getNeighbors = room.neighbors(this.location, searchRadius)
  def getContainmentNormal: Vector2d = {
    val probe = this.location + (currentVelocity.normalize() * containmentProbeDistance)
    room.getBoundaryNormal(probe)
  }

  def giveBrain(brain: PersonBrain) = this.brain = Some(brain)

  def updateVelocity(timePassed: Double) = {
    brain match {
      case Some(brain) =>
        currentVelocity = (currentVelocity + ((brain.targetVelocity() - currentVelocity).capMagnitude(maxAcc) * timePassed)).capMagnitude(maxSpd)
      case None =>
    }
  }

  def move(timePassed: Double) = location += currentVelocity * timePassed

  def facing = math.atan2(currentVelocity.y, currentVelocity.x)

}


