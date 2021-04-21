package logic

abstract class PersonBrain(val body: PersonBody) {

  def targetVelocity: Vector2d

  def brakingCoefficient: Double = 1.0

  def setLogicParameters(params: Map[String, Double]) = {}

}