package com.jakespringer.nonsequitur.engine

import java.util.function.Supplier
import com.jakespringer.nonsequitur.engine.util.Wrapper

abstract class Signal[T](subscribers: List[Notifier] = List()) extends Notifier(subscribers) {  
  def get(): T
  
  def combine(first: Signal[T], others: Signal[T]*): Signal[T] = {
    val combined: Cell[T] = new Cell(get())
    first.send((x: T) => combined.set(x), true)
    others.foreach((s: Signal[T]) => s.send((x: T) => combined.set(x), true))
    combined
  }
  
  def map[R](f: Function[T, R]): Signal[R] = {
    val thus = this
    val cell = new Cell[R](f.apply(this.get()))
    cell.setWhen(this, () => f.apply(thus.get()))
    cell
  }
  
  def filter(predicate: Function[T, Boolean]): Signal[T] = {
    val current = get()
    val signal = new Cell[T](if (predicate.apply(current)) current else /* TODO: FIGURE OUT WHAT GOES HERE -->*/current)
    this.send((x: T) => {
      if (predicate.apply(x)) {
        signal.event()
      }
    }, true)
    signal
  }
  
  override def until(predicate: () => Boolean): Signal[T] = {
    val thus = this
    new Signal[T](List(this)) {
      override def event(): Unit = {
        if (predicate.apply()) {
          destroy()
        } else {
          super.event()
        }
      }
      
      override def get(): T = thus.get()
    }
  }
  
  override def until(trigger: Notifier): Signal[T] = {
    val wrapper = new Wrapper[Destructible]()
    val signalUntil = distinct()
    wrapper.value = trigger.subscribe(() => {
      signalUntil.destroy()
      wrapper.value.destroy()
    }, true)
    signalUntil
  }
  
  override def untilNot(antiPredicate: () => Boolean) = until(() => !antiPredicate.apply())
  
  override def distinct(): Signal[T] = {
    val thus = this
    new Signal[T](List(this)) {
      def get(): T = thus.get()
    }
  }
  
  def send(consumer: T => _, weak: Boolean = false): Destructible = {
    subscribe(() => consumer.apply(get()), weak)
  }
}

object Signal {
  def integrate(v: Signal[Double], du: Signal[Double] = new Signal[Double] { def get(): Double = 1.0 }, c: Double = 0.0): Signal[Double] = {
    val storage = new Wrapper[Double](c)
    v.map((x: Double) => {
      storage.value += x * du.get()
      storage.value
    })
  }
  
  def derivate(v: Signal[Double], du: Signal[Double] = new Signal[Double] { def get(): Double = 1.0 }): Signal[Double] = {
    val storage = new Wrapper[Double](v.get())
    v.map((x: Double) => {
      val temp = storage.value
      storage.value = x
      (storage.value - temp) / du.get()
    })
  }
}
