package logic

abstract class PersonBrain(val body: PersonBody) {

  def targetVelocity(): Vector2d

}