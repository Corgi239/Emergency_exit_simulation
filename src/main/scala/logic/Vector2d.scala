package logic

case class Vector2d(var x: Double, var y: Double) {

  def +(other: Vector2d) = Vector2d(this.x + other.x, this.y + other.y)
  def -(other: Vector2d) = Vector2d(this.x - other.x, this.y - other.y)
  def *(scale: Double) = Vector2d(this.x * scale, this.y * scale)
  def clockwise() = Vector2d(-this.y, this.x)
  def counterclockwise() = Vector2d(this.y, -this.x)
  def normalize() = {
    if (!this.isZero) this * (1.0/this.magnitude) else Vector2d.ZeroVector()
  }
  def capMagnitude(cap: Double) = {
    if (!this.isZero) this * math.min(1.0, cap/this.magnitude) else Vector2d.ZeroVector()
  }

  def magnitude = math.hypot(this.x, this.y)
  def coordinates = (this.x, this.y)
  def distance(other: Vector2d) = math.hypot(this.x - other.x, this.y - other.y)
  def angleBetween(other: Vector2d) = {
    val angle1 = Math.atan2(this.y, this.x)
    val angle2 = Math.atan2(other.y, other.x)
    (angle2 - angle1).toDegrees
  }
  def isZero = (x == 0.0 && y == 0.0)

}

object Vector2d {
  def ZeroVector() = Vector2d(0.0, 0.0)
}
