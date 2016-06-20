package com.jakespringer.react

import scala.ref.WeakReference

class Destructible(protected[react] var str: Boolean = false) {
  private var children: List[Destructible] = List()
  private var parents: List[Destructible] = List()
  private var destroyed = false
  
  def isDestroyed = destroyed
  
  def destroy(): Unit = {
    if (destroyed) {
      ()
    } else {
      destroyed = true
      children.foreach(_.destroy())
      parents.foreach((x: Destructible) => {
        x.removeChild(this)
        if (x.children.isEmpty && !x.str) x.destroy()
      })
    }
  }
    
  protected[react] def removeChild(child: Destructible): Unit = {
    children = children diff List(child)
    if (children isEmpty) destroy()
  }
  
  protected[react] def addParent(parent: Destructible): Unit = {
    parent.children = parent.children :+ this
    parents = parents :+ parent
  }
}