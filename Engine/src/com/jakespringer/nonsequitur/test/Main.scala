package com.jakespringer.nonsequitur.test

import com.jakespringer.nonsequitur.engine.Cell
import com.jakespringer.nonsequitur.engine.Signal

object Main {
  def main(args: Array[String]): Unit = {
    println("Starting")
    val s = new Cell[Double](0.0)
    val integral = Signal.integrate(s)
    val f = integral.filter(_ < 100.0)
    val b = s.untilNot(() => integral.get() < 100.0).send((x: Double) => println("b = " + x))
    val k = integral.send((x: Double) => println("k = " + x))
    for (a <- 0 to 500) {
      s.set(a)
    }
  }
}