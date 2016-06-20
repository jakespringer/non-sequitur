package com.jakespringer.react.lwjgl

trait Renderer {
  def resize(width: Int, height: Int): Unit
  def initialize(): Unit
  def beginRendering(): Unit
  def endRendering()
}