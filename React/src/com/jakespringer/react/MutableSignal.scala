package com.jakespringer.react

class MutableSignal[T] (
  private var value: T
) extends Signal[T] {

  override def get(): T = value
  
  def set(newValue: T): Unit = {
    value = newValue
    event()
  }
  
  def edit(editFunction: T => T): Unit = {
    set(editFunction.apply(value))
  }
  
  def setWhen(notifier: Notifier, setFunction: Function0[T]): Destructible = {
    notifier.subscribe(() => set(setFunction.apply()))
  }
  
  def editWhen(notifier: Notifier, editFunction: Function[T, T]): Destructible = {
    notifier.subscribe(() => edit(editFunction))
  }
  
  override def setStrong(str: Boolean): MutableSignal[T] = {
    super.setStrong(str)
    this
  }
}
