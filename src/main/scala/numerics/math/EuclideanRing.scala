package numerics.math

import scala.{specialized => spec}
import scala.math.{abs, ceil, floor}

trait EuclideanRing[@spec(Int,Long,Float,Double) A] extends Ring[A] {
  def quot(a:A, b:A):A
  def mod(a:A, b:A):A
  def quotmod(a:A, b:A) = (quot(a, b), mod(a, b))
}

trait EuclideanRingOps[@spec(Int,Long,Float,Double) A] {
  val lhs:A
  val n:EuclideanRing[A]

  def /~(rhs:A) = n.quot(lhs, rhs)
  def %(rhs:A) = n.mod(lhs, rhs)
  def /%(rhs:A) = (n.quot(lhs, rhs), n.mod(lhs, rhs))
}

object EuclideanRing {
  implicit object IntIsEuclideanRing extends IntIsEuclideanRing
  implicit object LongIsEuclideanRing extends LongIsEuclideanRing
  implicit object FloatIsEuclideanRing extends FloatIsEuclideanRing
  implicit object DoubleIsEuclideanRing extends DoubleIsEuclideanRing
  implicit object BigIntIsEuclideanRing extends BigIntIsEuclideanRing
  implicit object BigDecimalIsEuclideanRing extends BigDecimalIsEuclideanRing
  implicit object RationalIsEuclideanRing extends RationalIsEuclideanRing
  implicit def complexIsEuclideanRing[A:Fractional:Exponential] = new ComplexIsEuclideanRing
  implicit object RealIsEuclideanRing extends RealIsEuclideanRing
}


trait IntIsEuclideanRing extends EuclideanRing[Int] with IntIsRing {
  def quot(a:Int, b:Int) = a / b
  def mod(a:Int, b:Int) = a % b
}

trait LongIsEuclideanRing extends EuclideanRing[Long] with LongIsRing {
  def quot(a:Long, b:Long) = a / b
  def mod(a:Long, b:Long) = a % b
}

trait FloatIsEuclideanRing extends EuclideanRing[Float] with FloatIsRing {
  def quot(a:Float, b:Float) = {
    val d = a / b
    if (d < 0.0) ceil(d).toFloat else floor(d).toFloat
  }
  def mod(a:Float, b:Float) = a % b
}

trait DoubleIsEuclideanRing extends EuclideanRing[Double] with DoubleIsRing {
  def quot(a:Double, b:Double) = {
    val d = a / b
    if (d < 0.0) ceil(d) else floor(d)
  }
  def mod(a:Double, b:Double) = a % b
}

trait BigIntIsEuclideanRing extends EuclideanRing[BigInt] with BigIntIsRing {
  def quot(a:BigInt, b:BigInt) = a / b
  def mod(a:BigInt, b:BigInt) = a % b
}

trait BigDecimalIsEuclideanRing extends EuclideanRing[BigDecimal] with BigDecimalIsRing {
  def quot(a:BigDecimal, b:BigDecimal) = a.quot(b)
  def mod(a:BigDecimal, b:BigDecimal) = a % b
}

trait RationalIsEuclideanRing extends EuclideanRing[Rational] with RationalIsRing {
  def quot(a:Rational, b:Rational) = a.quot(b)
  def mod(a:Rational, b:Rational) = a % b
}

class ComplexIsEuclideanRing[A](implicit f:Fractional[A], e:Exponential[A])
extends ComplexIsRing[A]()(f,e) with EuclideanRing[Complex[A]] {
  override def quotmod(a:Complex[A], b:Complex[A]) = {
    // TODO: fix this when Fractional has a floor
    //val quotient = Complex(f.floor((a / b).real), f.zero)
    val quotient = Complex(f.fromDouble(floor(f.toDouble((a / b).real))), f.zero)(f, e)
    val modulus = a - (b * quotient)
    (quotient, modulus)
  }
  def quot(a:Complex[A], b:Complex[A]) = quotmod(a, b)._1
  def mod(a:Complex[A], b:Complex[A]) = quotmod(a, b)._2
}

trait RealIsEuclideanRing extends EuclideanRing[Real] with RealIsRing {
  def quot(a: Real, b: Real): Real = (a / b).toBigInt
  def mod(a: Real, b: Real): Real = a - quot(a, b) * b
}
