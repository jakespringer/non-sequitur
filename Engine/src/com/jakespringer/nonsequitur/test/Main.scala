package com.jakespringer.nonsequitur.test

import com.jakespringer.nonsequitur.engine.Cell
import com.jakespringer.nonsequitur.engine.Signal
import scala.ref.WeakReference
import java.util.ArrayList
import com.jakespringer.nonsequitur.engine.Destructible

object Main {
  var current = 0
  
  def createWeak(c: Signal[Double]): Destructible = {
    val v = current
    current += 1
    c.foreach((x: Double) => print(v + " "), weak=true);
  }
  
  def createObject(): WeakReference[Object] = {
    new WeakReference(new Object())
  }
  
  def main(args: Array[String]): Unit = {
    val sig = new Cell[Double](0.0)
    sig.subscribe(() => println())
    var list = List(createObject())
    while (true) {
      val s = createWeak(sig)
      sig.set(Math.random())
      list = list :+ createObject()
      println()
      list.foreach { x => print(x.get match { case Some(e) => "s " case None => "n "}) }
      println()
      Thread.sleep(300)
    }
  }
}
