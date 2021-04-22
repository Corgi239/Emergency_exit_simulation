import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalactic.Equality
import org.scalactic.TolerantNumerics
import logic._

class RoomTest extends AnyFlatSpec with Matchers {

  val epsilon = 1e-4
  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(epsilon)
  implicit val tupleOfDoublesEq: Equality[(Double, Double)] = (a: (Double, Double), b: Any) => {
    b match {
      case t: (Double, Double) => (a._1 === t._1 +- epsilon) & (a._2 === t._2 +- epsilon)
      case _ => false
    }
  }

  val room = new Room(RoomConfig(Vector[(Double, Double)](), 100, 100, 0.1, Vector2d(100, 45)))

  "Room" should "calculate the Boundry Normal correctly" in {
    assert(room.getBoundaryNormal(Vector2d(-1, -1)) === Vector2d(1, 1).normalize())
    assert(room.getBoundaryNormal(Vector2d(1, -1)) === Vector2d(0, 1).normalize())
    assert(room.getBoundaryNormal(Vector2d(101, -1)) === Vector2d(-1, 1).normalize())
    assert(room.getBoundaryNormal(Vector2d(-1, 1)) === Vector2d(1, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(1, 1)) === Vector2d(0, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(101, 1)) === Vector2d(-1, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(-1, 50)) === Vector2d(1, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(1, 50)) === Vector2d(0, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(101, 50)) === Vector2d(0, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(-1, 85)) === Vector2d(1, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(1, 85)) === Vector2d(0, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(101, 85)) === Vector2d(-1, 0).normalize())
    assert(room.getBoundaryNormal(Vector2d(-1, 101)) === Vector2d(1, -1).normalize())
    assert(room.getBoundaryNormal(Vector2d(1, 101)) === Vector2d(0, -1).normalize())
    assert(room.getBoundaryNormal(Vector2d(101, 101)) === Vector2d(-1, -1).normalize())
  }
}
