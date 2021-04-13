package logic

class SimpleExitBrain(body: PersonBody) extends PersonBrain(body) {

  var seekingWeight: Double = 10.0
  var separationWeight: Double = 60.0
  var containmentWeight: Double = 30.0

  private def seekingComponent: Vector2d = {
    val goalLocation = body.getExitMiddle
    (goalLocation - body.location).normalize()
  }

  private def separationComponent: Vector2d = {

    def scalingFunction(dist: Double): Double = 1.0 / math.pow(dist, 2)

    body.getNeighbors.map( p => (body.location - p._1.location) * scalingFunction(p._2)).foldLeft(Vector2d(0, 0))( _ + _ )

  }

  private def containmentComponent: Vector2d = body.getContainmentNormal

  private def brakingCoefficient: Double = {
    val visiblePeople = body.getNeighborsInfront
    if (visiblePeople.nonEmpty) {
      visiblePeople.map( p => p._1.gasRatio).min
    } else {
      1.0
    }
  }

  override def targetVelocity(): Vector2d = ((seekingComponent * seekingWeight) + (separationComponent * separationWeight) + (containmentComponent * containmentWeight)) * brakingCoefficient

}