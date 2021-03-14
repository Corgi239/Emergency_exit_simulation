import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import logic.Vector2d
import org.scalactic.Equality
import org.scalactic.TolerantNumerics


class Vector2dTest extends AnyFlatSpec with Matchers {

  val epsilon = 1e-4
  implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(epsilon)
  implicit val tupleOfDoublesEq: Equality[(Double, Double)] = (a: (Double, Double), b: Any) => {
    b match {
      case t: (Double, Double) => (a._1 === t._1 +- epsilon) & (a._2 === t._2 +- epsilon)
      case _ => false
    }
  }

  "Vector" should "store the coordinates provided in the constructor" in {
    val testVector = Vector2d(2.0, 3.5)
    assert(testVector.coordinates === (2.0, 3.5))
  }

  "The sum and difference of two vectors" should "store the correct coordinates" in {
    val testVector1 = Vector2d(1.1, -3.2)
    val testVector2 = Vector2d(0.9, 4.0)
    val sumVector = testVector1 + testVector2
    assert(sumVector.coordinates === (2.0, 0.8))
    val diffVector = testVector1 - testVector2
    assert(diffVector.coordinates === (0.2, -7.2))
  }

  "Vector" should "scale correctly" in {
    val testVector = Vector2d(2.5, -7.9)
    var scaledVector = testVector * 4.2
    assert(scaledVector.coordinates === (2.5 * 4.2, -7.9 * 4.2))
    scaledVector = testVector * -0.3
    assert(scaledVector.coordinates === (2.5 * -0.3, -7.9 * -0.3))
  }

  "Vector" should "calculate its magnitude correctly" in {
    var testVector = Vector2d(3.0, 4.0)
    assert(testVector.magnitude === 5.0)
    testVector = Vector2d(-12.0, 16.0)
    assert(testVector.magnitude === 20.0)
  }

  "Vector" should "normalize correctly" in {
    val testVector = Vector2d(4.0, -3.0)
    val normalizedVector = testVector.normalize()
    assert(normalizedVector.coordinates === (0.8, -0.6))
  }

  "Vector" should "cap magnitude correctly" in {
    var testVector = Vector2d(7.3, 2.1)
    var cappedVector = testVector.capMagnitude(1.0)
    assert(cappedVector.coordinates === testVector.normalize().coordinates)
    testVector = Vector2d(0.2, 0.3)
    cappedVector = testVector.capMagnitude(1.0)
    assert(cappedVector.coordinates === testVector.coordinates)
  }

}
