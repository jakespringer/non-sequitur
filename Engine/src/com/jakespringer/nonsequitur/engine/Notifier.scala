package com.jakespringer.nonsequitur.engine

import com.jakespringer.nonsequitur.engine.util.Wrapper
import com.jakespringer.nonsequitur.engine.util.Wrapper
import com.jakespringer.nonsequitur.engine.util.Wrapper
import com.jakespringer.nonsequitur.engine.util.Wrapper

class Notifier(notifiers: List[Notifier] = List()) extends Destructible {
  protected[engine] var subscribers: List[() => Unit] = List()
  
  notifiers.foreach(x => x.subscribe(() => this.event()))

  def subscribe(listener: () => Unit): Destructible = {
    subscribers = subscribers :+ listener
    var dc = new DestructibleContainer(listener, this)
    dc.addParent(this)
    dc
  }
  
  protected def event(): Unit = {
    subscribers.foreach(_.apply())
  }
  
  def combine(first: Notifier, others: Notifier*): Notifier = {
    new Notifier(others.toList :+ first)
  }
  
  def until(predicate: () => Boolean): Notifier = {
    new Notifier(List(this)) {
      override def event(): Unit = if (predicate.apply()) {
          destroy();
        } else {
          super.event()
        }
    }
  }
  
  def until(trigger: Notifier): Notifier = {
    var wrapper = new Wrapper[Destructible]()
    val signalUntil = distinct()
    wrapper.value = trigger.subscribe(() => {
      signalUntil.destroy()
      wrapper.value.destroy()
    })
    signalUntil
  }
  
  def untilNot(antiPredicate: () => Boolean): Notifier = until(() => !antiPredicate.apply())
  
  def filter(predicate: () => Boolean): Notifier = {
    val notifier = new Notifier()
    this.subscribe(() => {
      if (predicate.apply()) {
        notifier.event()
      }
    })
    notifier
  }
  
  def filterNot(antiPredicate: () => Boolean): Notifier = filter(() => !antiPredicate.apply())
  
  def count(): Signal[Int] = {
    val wrapper = new Wrapper[Int](0)
    new Signal[Int](List(this)) {
      def get(): Int = {
        wrapper.value += 1
        wrapper.value
      }
    }
  }
  
  def distinct(): Notifier = {
    new Notifier(List(this))
  }
  
  override def destroy() {
    super.destroy()
    subscribers = List()
  }
}