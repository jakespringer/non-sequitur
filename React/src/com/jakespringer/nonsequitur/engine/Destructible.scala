package com.jakespringer.nonsequitur.engine

import scala.ref.WeakReference

class Destructible(protected[engine] var str: Boolean = false) {
  private var children: List[Destructible] = List()
  private var parents: List[Destructible] = List()
  private var destroyed = false
  
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
    
  protected[engine] def removeChild(child: Destructible): Unit = {
    children = children diff List(child)
    if (children isEmpty) destroy()
  }
  
  protected[engine] def addParent(parent: Destructible): Unit = {
    parent.children = parent.children :+ this
    parents = parents :+ parent
  }
}