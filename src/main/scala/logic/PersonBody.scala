package logic

class PersonBody(var location: Vector2d, room: Room) {

  private var brain: Option[PersonBrain] = None
  private var currentVelocity: Vector2d = Vector2d(1, 0)
  private var brakingCoefficient: Double = 1.0

  private var maxAcc: Double = 0.0001
  private var maxSpd: Double = 0.05
  private var searchRadius = 25.0 + 5
  private val containmentProbeDistance = 5.0
  private val fov = 30.0

  def maxSpeed = maxSpd
  def setMaxSpeed(updMaxSpd: Double) = maxSpd = updMaxSpd
  def setSearchRadius(updRadius: Double) = searchRadius = updRadius
  def setLogicParameters(params: Map[String, Double]) = {
    brain match {
      case Some(b: PersonBrain) => b.setLogicParameters(params)
      case None =>
    }
  }

  def getExitMiddle = room.exitMiddle

  def getNeighbors = room.neighbors(this.location, searchRadius)
  def getNeighborsInfront = {
    val res = this.getNeighbors.filter( p => Math.abs(p._1.location.angleBetween(this.location)) <= fov )
    res
  }

  def getContainmentNormal: Vector2d = {
    val probe1 = this.location + (currentVelocity.normalize() * containmentProbeDistance)
    val probe2 = this.location + ((currentVelocity.clockwise() + currentVelocity).normalize() * (containmentProbeDistance * 2))
    val probe3 = this.location + ((currentVelocity.counterclockwise() + currentVelocity).normalize() * (containmentProbeDistance * 2))
    (room.getBoundaryNormal(probe1) * 3) + room.getBoundaryNormal(probe2) + room.getBoundaryNormal(probe3)
  }

  def giveBrain(brain: PersonBrain) = this.brain = Some(brain)

  def updateVelocity(timePassed: Double) = {
    brain match {
      case Some(brain) =>
        currentVelocity = (currentVelocity + ((brain.targetVelocity - currentVelocity).capMagnitude(maxAcc) * timePassed)).capMagnitude(maxSpd)
        brakingCoefficient = brain.brakingCoefficient
      case None =>
    }
  }

  def move(timePassed: Double) = {
    location += currentVelocity * timePassed * brakingCoefficient
    if (room.getBoundaryNormal(this.location) != Vector2d(0, 0)) {
      this.location.x = Math.min(this.location.x, room.config.roomWidth - 0.1)
    }
  }

  def facing = math.atan2(currentVelocity.y, currentVelocity.x)

  def gasRatioBeforeBraking = this.currentVelocity.magnitude / maxSpd

  def gasRatio = gasRatioBeforeBraking * brakingCoefficient

}



