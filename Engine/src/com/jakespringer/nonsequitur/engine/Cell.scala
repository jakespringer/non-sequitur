package com.jakespringer.nonsequitur.engine

class Cell[T] (
  private var value: T
) extends Signal[T] {
  @Override
  def get(): T = value
  
  def set(newValue: T): Unit = {
    value = newValue
    event()
  }
  
  def edit(editFunction: T => T): Unit = {
    set(editFunction.apply(value))
  }
}