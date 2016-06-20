package com.jakespringer.react.util

import java.util.function.BinaryOperator
import java.util.function.UnaryOperator

object Vec2 {
  val IDENTITY = Vec2(0.0, 0.0)
  val UNIT = Vec2(1.0, 0.0)
  val TOLERANCE: Double = 1E-12

  def fromPolar(r: Double, t: Double): Vec2 = Vec2(r * Math.cos(t), r * Math.sin(t))

  def randomCircle(r: Double): Vec2 = randomShell(r * Math.random())

  def randomShell(r: Double): Vec2 = fromPolar(r, 2 * Math.PI * Math.random())

  def randomSquare(r: Double): Vec2 = Vec2(Math.random() * 2 * r - r, Math.random() * 2 * r - r)
  
  def apply(x: Double, y: Double): Vec2 = new Vec2(x, y)
}

class Vec2(val x: Double, val y: Double) {
  
  def add(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y)

  def clamp(LL: Vec2, UR: Vec2): Vec2 = {
    var nx = x
    var ny = y
    if (nx < LL.x) {
      nx = LL.x
    } else if (nx > UR.x) {
      nx = UR.x
    }
    if (ny < LL.y) {
      ny = LL.y
    } else if (ny > UR.y) {
      ny = UR.y
    }
    Vec2(nx, ny)
  }

  def containedBy(v1: Vec2, v2: Vec2): Boolean = {
    val q1 = v1.quadrant(this)
    val q2 = v2.quadrant(this)
    q1 != q2 && q1 % 2 == q2 % 2
  }

  def cross(other: Vec2): Double = x * other.y - y * other.x

  def direction(): Double = Math.atan2(y, x)

  def divide(d: Double): Vec2 = Vec2(x / d, y / d)

  def divide(v: Vec2): Vec2 = Vec2(x / v.x, y / v.y)

  def dot(other: Vec2): Double = x * other.x + y * other.y

  override def equals(o: Any): Boolean = {
    if (o.isInstanceOf[Vec2]) {
      val v = o.asInstanceOf[Vec2]
      return Math.abs(x - v.x) <= Vec2.TOLERANCE && Math.abs(y - v.y) <= Vec2.TOLERANCE
    }
    false
  }

  def interpolate(other: Vec2, amt: Double): Vec2 = multiply(amt).add(other.multiply(1 - amt))

  def length(): Double = Math.sqrt(lengthSquared())

  def lengthSquared(): Double = x * x + y * y

  def multiply(d: Double): Vec2 = Vec2(x * d, y * d)

  def multiply(other: Vec2): Vec2 = Vec2(x * other.x, y * other.y)

  def normal(): Vec2 = Vec2(-y, x)

  def normalize(): Vec2 = {
    val len = length
    if (len == 0) Vec2.UNIT else multiply(1 / len)
  }

  def perComponent(u: UnaryOperator[Double]): Vec2 = Vec2(u.apply(x), u.apply(y))

  def perComponent(other: Vec2, u: BinaryOperator[Double]): Vec2 = Vec2(u.apply(x, other.x), u.apply(y, other.y))

  def quadrant(other: Vec2): Int = {
    if (other.x >= x) {
      if (other.y >= y) {
        1
      } else {
        4
      }
    } else if (other.y >= y) {
      2
    } else {
      3
    }
  }

  def reverse(): Vec2 = Vec2(-x, -y)

  def rotate(t: Double): Vec2 = Vec2(x * Math.cos(t) - y * Math.sin(t), x * Math.sin(t) + y * Math.cos(t))

  def subtract(other: Vec2): Vec2 = Vec2(x - other.x, y - other.y)

  override def toString(): String = ("(" + x.toFloat + ", " + y.toFloat + ")")
  
  def withLength(l: Double): Vec2 = if (l == 0.0) Vec2.IDENTITY else multiply(l / length)

  def withX(newx: Double): Vec2 = Vec2(newx, y)

  def withY(newy: Double): Vec2 = Vec2(x, newy)
}
