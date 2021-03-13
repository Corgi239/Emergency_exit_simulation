import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import logic.Vector2d
import org.scalactic.Equality


class Vector2dTest extends AnyFlatSpec with Matchers {

  implicit val tupleOfDoublesEq: Equality[(Double, Double)] = (a: (Double, Double), b: Any) => {
    val epsilon = 1e-4
    b match {
      case t: (Double, Double) => (a._1 === t._1 +- epsilon) & (a._2 === t._2 +- epsilon)
      case _ => false
    }
  }

  "Vector" should "store the coordinates provided in the constructor" in {
    val testVector = Vector2d(2.0, 3.5)
    assert(testVector.coordinates === (2.0, 3.5))
  }

  "The sum of two vectors" should "store the correct coordinates" in {
    val testVector1 = Vector2d(1.1, -3.2)
    val testVector2 = Vector2d(0.9, 4.0)
    val sumVector = testVector1 + testVector2
    assert(sumVector.coordinates === (2.0, 0.8))
  }

  "Vector" should "scale correctly" in {
    val testVector = Vector2d(2.5, -7.9)
    var scaledVector = testVector * 4.2
    assert(scaledVector.coordinates === (2.5 * 4.2, -7.9 * 4.2))
    scaledVector = testVector * -0.3
    assert(scaledVector.coordinates === (2.5 * -0.3, -7.9 * -0.3))
  }

}
