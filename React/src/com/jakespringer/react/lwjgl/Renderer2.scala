package com.jakespringer.react.lwjgl

import org.lwjgl.opengl.GL._
import org.lwjgl.opengl.GL11._
import com.jakespringer.react.util.Vec2
import com.jakespringer.react.util.Color4

class Renderer2 extends Renderer {
  var viewPos: Vec2 = Vec2(0, 0)
  var viewSize: Vec2 = Vec2(1, 1)
  var backgroundColor: Color4 = Color4.BLUE
  
  override def initialize(): Unit = {
  }

  override def resize(width: Int, height: Int): Unit = {
    Camera2.calculateViewport(aspectRatio(), width, height);
    Camera2.setProjection(ll(), ur());
    viewSize = Vec2(width, height)
  }

  override def beginRendering(): Unit = {
    glClear(GL_COLOR_BUFFER_BIT)
    glClearColor(backgroundColor.r.toFloat, backgroundColor.g.toFloat, backgroundColor.b.toFloat, backgroundColor.a.toFloat)
  }

  override def endRendering(): Unit = {
  }

  def aspectRatio(): Double = viewSize.x / viewSize.y

  def inView(pos: Vec2): Boolean = pos.containedBy(ll(), ur())

  def ll(): Vec2 = viewPos.subtract(viewSize.multiply(.5))

  def lr(): Vec2 = viewPos.add(viewSize.multiply(new Vec2(.5, -.5)))

  def nearInView(pos: Vec2, buffer: Vec2): Boolean = pos.containedBy(ll().subtract(buffer), ur().add(buffer))

  def ur(): Vec2 = viewPos.add(viewSize.multiply(.5))

  def ul(): Vec2 = viewPos.add(viewSize.multiply(new Vec2(-.5, .5)))
}