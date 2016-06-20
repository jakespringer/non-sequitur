package com.jakespringer.react

import java.util.function.Supplier
import com.jakespringer.react.util.Wrapper

abstract class Signal[T](subscribers: List[Notifier] = List()) extends Notifier(subscribers) {  
  def get(): T
  
  def combine(first: Signal[T], others: Signal[T]*): Signal[T] = {
    val combined: MutableSignal[T] = new MutableSignal(get())
    first.foreach((x: T) => combined.set(x))
    others.foreach((s: Signal[T]) => s.foreach((x: T) => combined.set(x)))
    combined
  }
  
  def map[R](f: Function[T, R]): Signal[R] = {
    val thus = this
    val cell = new MutableSignal[R](f.apply(this.get()))
    cell.setWhen(this, () => f.apply(thus.get()))
    cell
  }
  
  def filter(predicate: Function[T, Boolean]): Signal[T] = {
    val current = get()
    val signal = new MutableSignal[T](if (predicate(current)) current else /* TODO: FIGURE OUT WHAT GOES HERE -->*/ current)
    signal.setWhen(super.filter(() => predicate(get())), () => get())
    signal
  }
  
  override def until(predicate: Function0[Boolean]): Signal[T] = {
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
    })
    signalUntil
  }
  
  override def untilNot(antiPredicate: Function0[Boolean]) = until(() => !antiPredicate.apply())
  
  override def distinct(): Signal[T] = {
    val thus = this
    new Signal[T](List(this)) {
      def get(): T = thus.get()
    }
  }
  
  override def strong(): Signal[T] = {
    val notifier = distinct()
    notifier.setStrong(true)
    notifier
  }
  
  override def setStrong(str: Boolean): Signal[T] = {
    this.str = str
    this
  }
  
  def foreach(consumer: T => Any): Destructible = {
    subscribe(() => consumer.apply(get()))
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
  
  def derivativeOf(v: Signal[Double], du: Signal[Double] = new Signal[Double] { def get(): Double = 1.0 }): Signal[Double] = {
    val storage = new Wrapper[Double](v.get())
    v.map((x: Double) => {
      val temp = storage.value
      storage.value = x
      (storage.value - temp) / du.get()
    })
  }
}
