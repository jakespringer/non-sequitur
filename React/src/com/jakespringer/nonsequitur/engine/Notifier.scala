package com.jakespringer.nonsequitur.engine

import scala.ref.WeakReference

import com.jakespringer.nonsequitur.engine.util.Wrapper

class Notifier(notifiers: List[Notifier] = List()) extends Destructible {
  protected[engine] var subscribers: List[DestructibleContainer] = List()

  notifiers.foreach(x => x.subscribe(() => this.event()))

  def subscribe(listener: Function0[Unit]): Destructible = {
    val dc = new DestructibleContainer(listener, this)
    subscribers = subscribers :+ dc
    dc.addParent(this)
    dc
  }

  protected def event(): Unit = {
    subscribers.foreach(_.runnable())
  }

  def combine(first: Notifier, others: Notifier*): Notifier = {
    new Notifier(others.toList :+ first)
  }

  def until(predicate: Function0[Boolean]): Notifier = {
    new Notifier(List(this)) {
      override def event(): Unit = if (predicate()) {
        destroy();
      } else {
        super.event()
      }
    }
  }

  def until(trigger: Notifier): Notifier = {
    val wrapper = new Wrapper[Destructible]()
    val signalUntil = distinct()
    wrapper.value = trigger.subscribe(() => {
      signalUntil.destroy()
      wrapper.value.destroy()
    })
    signalUntil
  }

  def untilNot(antiPredicate: Function0[Boolean]): Notifier = until(() => !antiPredicate.apply())

  def filter(predicate: Function0[Boolean]): Notifier = {
    val notifier = new Notifier()
    this.subscribe(() => {
      if (predicate()) {
        notifier.event()
      }
    })
    notifier
  }

  def filterNot(antiPredicate: Function0[Boolean]): Notifier = filter(() => !antiPredicate.apply())

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
  
  def strong(): Notifier = {
    val notifier = distinct()
    notifier.setStrong(true)
    notifier
  }
  
  def setStrong(str: Boolean): Notifier = {
    this.str = str
    this
  }

  override def destroy() {
    super.destroy()
    subscribers = List()
  }
}