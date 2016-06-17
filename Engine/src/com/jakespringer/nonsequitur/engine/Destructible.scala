package com.jakespringer.nonsequitur.engine

import scala.ref.WeakReference

class Destructible {
  private var children: List[Destructible] = List()
  private var weakChildren: List[WeakReference[Destructible]] = List()
  private var parents: List[Destructible] = List()
  private var destroyed = false
  
  def destroy(): Unit = {
    if (destroyed) {
      ()
    } else {
      destroyed = true
      children.foreach(_.destroy())
      weakChildren.foreach { x =>
        x.get match {
          case Some(e) => e.destroy()
          case None => ()
        }
      }
      parents.foreach(_.removeChild(this))
    }
  }
    
  protected[engine] def removeChild(child: Destructible): Unit = {
    children = children diff List(child)
    weakChildren = weakChildren.filter(x => x.get match {
      case Some(e) => if (e.equals(child)) false else true
      case None => false
    })
    if (children isEmpty) destroy()
  }
  
  protected[engine] def addParent(parent: Destructible): Unit = {
    parent.children = parent.children :+ this
    parents = parents :+ parent
  }
  
  protected[engine] def addWeakParent(parent: Destructible): Unit = {
    parent.weakChildren = parent.weakChildren :+ new WeakReference[Destructible](this)
    parents = parents :+ parent
  }
}