package com.jakespringer.nonsequitur.engine

import java.util.function.Supplier
import com.jakespringer.nonsequitur.engine.util.Wrapper

abstract class Signal[T](subscribers: List[Notifier] = List()) extends Notifier(subscribers) {  
  def get(): T
  
  def combine(first: Signal[T], others: Signal[T]*): Signal[T] = {
    val combined: Cell[T] = new Cell(get())
    first.send((x: T) => combined.set(x))
    others.foreach((s: Signal[T]) => s.send((x: T) => combined.set(x)))
    combined
  }
  
  def map[R](f: Function[T, R]): Signal[R] = {
    val thus = this
    new Signal[R] {
      def get(): R = f.apply(thus.get())
    }
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
    var wrapper = new Wrapper[Destructible]()
    val signalUntil = distinct()
    wrapper.value = trigger.subscribe(() => {
      signalUntil.destroy()
      wrapper.value.destroy()
    })
    signalUntil
  }
  
  override def untilNot(antiPredicate: () => Boolean) = until(() => !antiPredicate.apply())
  
  override def distinct(): Signal[T] = {
    val thus = this
    new Signal[T](List(this)) {
      def get(): T = thus.get()
    }
  }
  
  def send(consumer: T => _): Destructible = {
    subscribe(() => consumer.apply(get()))
  }
}