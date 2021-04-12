package logic

class PersonBody(var location: Vector2d, room: Room) {

  private var brain: Option[PersonBrain] = None
  private var currentVelocity: Vector2d = Vector2d(0, 0)

  private var maxAcc: Double = 0.0001
  private var maxSpd: Double = 0.05
  private var searchRadius = 25.0
  private val containmentProbeDistance = 5.0

  def maxSpeed = maxSpd
  def setMaxSpeed(updMaxSpd: Double) = maxSpd = updMaxSpd

  def getExitMiddle = room.exitMiddle
  def getNeighbors = room.neighbors(this.location, searchRadius)
  def getContainmentNormal: Vector2d = {
    val probe1 = this.location + (currentVelocity.normalize() * containmentProbeDistance)
    val probe2 = this.location + ((currentVelocity.clockwise() + currentVelocity).normalize() * (containmentProbeDistance * 2))
    val probe3 = this.location + ((currentVelocity.counterclockwise() + currentVelocity).normalize() * (containmentProbeDistance * 2))
    room.getBoundaryNormal(probe1) + room.getBoundaryNormal(probe2) + room.getBoundaryNormal(probe3)
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


