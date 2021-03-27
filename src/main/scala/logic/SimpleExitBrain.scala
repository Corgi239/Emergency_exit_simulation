package logic

class SimpleExitBrain(body: PersonBody) extends PersonBrain(body) {

  var seekingWeight: Double = 10.0

  private def seekingComponent = {
    val goalLocation = body.getExitMiddle
    (goalLocation - body.location).normalize()
  }

  override def targetVelocity(): Vector2d = seekingComponent * seekingWeight

}