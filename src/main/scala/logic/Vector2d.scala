package logic

case class Vector2d(var x: Double, var y: Double) {

  def +(other: Vector2d) = Vector2d(this.x + other.x, this.y + other.y)
  def *(scale: Double) = Vector2d(this.x * scale, this.y * scale)

  def coordinates = (this.x, this.y)

}
