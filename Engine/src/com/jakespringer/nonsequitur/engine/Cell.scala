package com.jakespringer.nonsequitur.engine

class Cell[T] (
  private var value: T
) extends Signal[T] {
//  def Cell(initial: T, updateOn: EventStream*) = {
//    
//  }
  
  @Override
  def get(): T = value
  
  def set(newValue: T): Unit = {
    value = newValue
  }
  
  def edit(editFunction: T => T): Unit = {
    value = editFunction apply value
  }
}