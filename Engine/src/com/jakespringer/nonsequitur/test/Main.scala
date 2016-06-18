package com.jakespringer.nonsequitur.test

import com.jakespringer.nonsequitur.engine.Cell
import com.jakespringer.nonsequitur.engine.Signal
import scala.ref.WeakReference
import java.util.ArrayList
import com.jakespringer.nonsequitur.engine.Destructible

object Main {
  var current = 0
  
  def createWeak(c: Signal[Double]): Unit = {
    val v = current
    current += 1
    new WeakReference(c.send((x: Double) => print(v + " "), weak=true));
  }
  
  def createObject(): WeakReference[Object] = {
    new WeakReference(new Object())
  }
  
  def main(args: Array[String]): Unit = {
    val sig = new Cell[Double](0.0)
    sig.subscribe(() => println())
    var list = List(createObject())
    while (true) {
      sig.set(Math.random())
      createWeak(sig)
      list = list :+ createObject()
      println()
      list.foreach { x => print(x.get match { case Some(e) => "s " case None => "n "}) }
      println()
      Thread.sleep(300)
      System.gc()
    }
  }
}