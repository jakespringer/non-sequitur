package com.jakespringer.nonsequitur.engine

class Destructible {
  private var children: List[Destructible] = List()
  private var parents: List[Destructible] = List()
  private var destroyed = false
  
  def destroy(): Unit = {
    if (destroyed) {
      ()
    } else {
      destroyed = true
      children.foreach { _ destroy }
      parents.foreach { _ removeChild this }
    }
  }
  
  def removeChild(child: Destructible): Unit = {
    children = children diff List(child)
    if (children isEmpty) destroy()
  }
  
  def addParent(parent: Destructible): Unit = {
    parent.children = parent.children :+ this
    parents = parents :+ parent
  }
}