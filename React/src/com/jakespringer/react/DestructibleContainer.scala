package com.jakespringer.react

class DestructibleContainer (
  val runnable: () => Unit,
  val notifier: Notifier
) extends Destructible {
  override def destroy() {
    super.destroy()
    notifier.subscribers = notifier.subscribers diff List(this)
  }
}