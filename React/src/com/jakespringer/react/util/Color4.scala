package com.jakespringer.react.util

import java.nio.FloatBuffer
import org.lwjgl.opengl.GL11

object Color4 {

  val WHITE = new Color4(1, 1, 1, 1)

  val BLACK = new Color4(0, 0, 0, 1)

  val RED = new Color4(1, 0, 0, 1)

  val GREEN = new Color4(0, 1, 0, 1)

  val BLUE = new Color4(0, 0, 1, 1)

  val PURPLE = new Color4(.5, 0, 1, 1)

  val YELLOW = new Color4(1, 1, 0, 1)

  val ORANGE = new Color4(1, 0.5, 0, 1)

  val TRANSPARENT = new Color4(0, 0, 0, 0)

  def gray(d: Double): Color4 = new Color4(d, d, d)

  def random(): Color4 = new Color4(Math.random(), Math.random(), Math.random())
  
  def apply(r: Double, g: Double, b: Double, a: Double): Color4 = new Color4(r, g, b, a)
  
  def apply(r: Double, g: Double, b: Double): Color4 = new Color4(r, g, b, 1)
  
  def apply(): Color4 = Color4(1, 1, 1, 1)
}

class Color4(val r: Double, val g: Double, val b: Double, val a: Double) {

  def this(r: Double, g: Double, b: Double) {
    this(r, g, b, 1)
  }

  def this() {
    this(1, 1, 1, 1)
  }

  def glClearColor() {
    GL11.glClearColor(r.toFloat, g.toFloat, b.toFloat, a.toFloat)
  }

  def glColor() {
    GL11.glColor4d(r, g, b, a)
  }

  def multiply(d: Double): Color4 = new Color4(r * d, g * d, b * d, a)

  override def toString(): String = ("Color(" + r + ", " + g + ", " + b + ", " + a + ")")

  def withR(r: Double): Color4 = new Color4(r, g, b, a)

  def withG(g: Double): Color4 = new Color4(r, g, b, a)

  def withB(b: Double): Color4 = new Color4(r, g, b, a)

  def withA(a: Double): Color4 = new Color4(r, g, b, a)
}