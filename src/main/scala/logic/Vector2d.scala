package logic

case class Vector2d(var x: Double, var y: Double) {

  def +(other: Vector2d) = Vector2d(this.x + other.x, this.y + other.y)
  def -(other: Vector2d) = Vector2d(this.x - other.x, this.y - other.y)
  def *(scale: Double) = Vector2d(this.x * scale, this.y * scale)
  def magnitude = math.hypot(this.x, this.y)
  def normalize() = this * (1.0/this.magnitude)
  def capMagnitude(cap: Double) = this * math.min(1.0, cap/this.magnitude)

  def coordinates = (this.x, this.y)
  def distance(other: Vector2d) = math.hypot(this.x - other.x, this.y - other.y)
  def angleBetween(other: Vector2d) = {
    val angle1 = Math.atan2(this.y, this.x)
    val angle2 = Math.atan2(other.y, other.x)
    (angle2 - angle1).toDegrees
  }

}
