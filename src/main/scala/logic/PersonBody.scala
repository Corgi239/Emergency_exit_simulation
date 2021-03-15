package logic

class PersonBody(var location: Vector2d) {

 private var brain: Option[PersonBrain] = None
 private var currentVelocity: Vector2d = Vector2d(1, 2)

 def giveBrain(brain: PersonBrain) = this.brain = Some(brain)

 def updateVelocity() = {
  brain match {
   case Some(brain) =>
      currentVelocity = currentVelocity + (brain.targetVelocity() - currentVelocity).capMagnitude(PersonBody.MAX_ACC)
   case None =>
  }
 }

 def move(timePassed: Double) = location += currentVelocity * (PersonBody.MAX_SPD * timePassed)

}

object PersonBody {
  private val MAX_ACC: Double = 0.005
  private val MAX_SPD: Double = 0.02
}