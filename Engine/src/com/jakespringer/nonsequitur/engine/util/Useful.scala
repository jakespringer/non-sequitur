package com.jakespringer.nonsequitur.engine.util

import com.jakespringer.nonsequitur.engine.Signal

/*
 * TODO: Rename me!
 */
object Useful {
  def integrate(v: Signal[Double], du: Signal[Double] = new Signal[Double] { def get(): Double = 1.0 }, c: Double = 0.0): Signal[Double] = {
    var storage = new Wrapper[Double](c)
    new Signal[Double](List(v)) {
      def get(): Double = {
        storage.value += v.get() * du.get()
        storage.value
      }
    }
  }
  
  def derivate(v: Signal[Double], du: Signal[Double] = new Signal[Double] { def get(): Double = 1.0 }): Signal[Double] = {
    var storage = new Wrapper[Double](v.get())
    new Signal[Double](List(v)) {
      def get(): Double = {
        val temp = storage.value
        storage.value = v.get()
        (storage.value - temp) / du.get()
      }
    }
  }
}