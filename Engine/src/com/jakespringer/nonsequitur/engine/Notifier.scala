package com.jakespringer.nonsequitur.engine

import scala.ref.WeakReference

import com.jakespringer.nonsequitur.engine.util.Wrapper

class Notifier(notifiers: List[Notifier] = List(), weak: Boolean = false) extends Destructible {
  protected[engine] var subscribers: List[WeakReference[DestructibleContainer]] = List()

  notifiers.foreach(x => x.subscribe(() => this.event(), weak=weak))

  def subscribe(listener: () => Unit, weak: Boolean = false): Destructible = {
    val dc = new DestructibleContainer(listener, this)
    subscribers = subscribers :+ new WeakReference(dc)
    if (weak) dc.addWeakParent(this) else dc.addParent(this)
    dc
  }

  protected def event(): Unit = {
    var deletionList = List[WeakReference[DestructibleContainer]]()
    subscribers.foreach((x: WeakReference[DestructibleContainer]) => {
      x.get match {
        case Some(e) => e.runnable()
        case None => deletionList = deletionList :+ x
      }
    })
    subscribers = subscribers diff deletionList
  }

  def combine(first: Notifier, others: Notifier*): Notifier = {
    new Notifier(others.toList :+ first)
  }

  def until(predicate: () => Boolean): Notifier = {
    new Notifier(List(this), weak=true) {
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
    }, true)
    signalUntil
  }

  def untilNot(antiPredicate: () => Boolean): Notifier = until(() => !antiPredicate.apply())

  def filter(predicate: () => Boolean): Notifier = {
    val notifier = new Notifier()
    this.subscribe(() => {
      if (predicate()) {
        notifier.event()
      }
    }, weak=true)
    notifier
  }

  def filterNot(antiPredicate: () => Boolean): Notifier = filter(() => !antiPredicate.apply())

  def count(): Signal[Int] = {
    val wrapper = new Wrapper[Int](0)
    new Signal[Int](List(this), weak=true) {
      def get(): Int = {
        wrapper.value += 1
        wrapper.value
      }
    }
  }

  def distinct(): Notifier = {
    new Notifier(List(this), weak=true)
  }

  override def destroy() {
    super.destroy()
    subscribers = List()
  }
}