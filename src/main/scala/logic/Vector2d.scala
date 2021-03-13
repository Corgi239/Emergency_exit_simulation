package logic

case class Vector2d(var x: Double, var y: Double) {

  def +(other: Vector2d) = Vector2d(this.x + other.x, this.y + other.y)
  def *(scale: Double) = Vector2d(this.x * scale, this.y * scale)
  def magnitude = math.hypot(this.x, this.y)
  def normalize() = this * (1.0/this.magnitude)

  def coordinates = (this.x, this.y)

}
