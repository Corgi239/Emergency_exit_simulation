package logic

class SimpleExitBrain(body: PersonBody) extends PersonBrain(body) {

  var seekingWeight: Double = 10.0
  var separationWeight: Double = 20.0

  private def seekingComponent: Vector2d = {
    val goalLocation = body.getExitMiddle
    (goalLocation - body.location).normalize()
  }

  private def separationComponent: Vector2d = {

    def scalingFunction(dist: Double): Double = 1.0 / math.pow(dist, 2)

    body.getNeighbors.map(body.location - _.location).map(v => v * scalingFunction(v.magnitude) ).foldLeft(Vector2d(0, 0))( _ + _ )

  }

  override def targetVelocity(): Vector2d = (seekingComponent * seekingWeight) + (separationComponent * separationWeight)

}