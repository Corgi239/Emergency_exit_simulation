package logic

class TestBrain(body: PersonBody) extends PersonBrain(body){

  override def targetVelocity(): Vector2d = Vector2d(1, -1)
}
