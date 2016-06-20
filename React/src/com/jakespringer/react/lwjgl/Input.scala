package com.jakespringer.react.lwjgl

import com.jakespringer.react.Signal
import com.jakespringer.react.MutableSignal
import com.jakespringer.react.Notifier
import org.lwjgl.glfw.GLFW

object Input {
  private val stepSignal: Signal[Double] = new MutableSignal[Double](0.0).setStrong(true)
  private var keyStateSignals = Map[Int, MutableSignal[Boolean]]()
  private var whenKeySignals = Map[Int, Notifier]()
  private var whileKeySignals = Map[Int, Signal[Double]]()

  def keyState(key: Int): Signal[Boolean] = {
    if (keyStateSignals contains key) {
      keyStateSignals(key)
    } else {
      val signal = new MutableSignal[Boolean](false)
      signal.setStrong(true)
      keyStateSignals = keyStateSignals + (key -> signal)
      signal
    }
  }

  def whenKey(key: Int, pressed: Boolean): Notifier = {
    if (whenKeySignals contains key) {
      whenKeySignals(key)
    } else {
      val signal = keyState(key).filter(_ == pressed).strong()
      whenKeySignals = whenKeySignals + (key -> signal)
      signal
    }
  }

  def whileKey(key: Int, pressed: Boolean): Signal[Double] = {
    if (whileKeySignals contains key) {
      whileKeySignals(key)
    } else {
      val s = keyState(key)
      val signal = stepSignal.filter((x: Double) => s.get() == pressed).strong()
      whileKeySignals = whileKeySignals + (key -> signal)
      signal
    }
  }

  def initialize(window: Window[_], step: Signal[Double]): Unit = {
    step.foreach(stepSignal.asInstanceOf[MutableSignal[Double]].set(_))
    window.setKeyCallback((window: Long, key: Int, scancode: Int, action: Int, mods: Int) => {
      if (keyStateSignals contains key) {
        keyStateSignals(key).set(action match {
          case GLFW.GLFW_PRESS => true
          case GLFW.GLFW_RELEASE => false
          case _ => false
        })
      }
    })
  }
}