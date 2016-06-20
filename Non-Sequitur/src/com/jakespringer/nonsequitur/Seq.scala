package com.jakespringer.nonsequitur

import org.lwjgl.glfw.GLFW

import com.jakespringer.react.MutableSignal
import com.jakespringer.react.Notifier
import com.jakespringer.react.Signal
import com.jakespringer.react.lwjgl.Input
import com.jakespringer.react.lwjgl.Renderer2
import com.jakespringer.react.lwjgl.Window
import com.jakespringer.react.res.Resource
import com.jakespringer.react.lwjgl.GLUtil._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._

object Seq {  
  val window = new Window[Renderer2](new Renderer2())
  val step: Signal[Double] = new MutableSignal[Double](0.0).setStrong(true)
  val time = Signal.integrate(step).setStrong(true)
  val draw: Notifier = new MutableSignal[Unit](()).setStrong(true)
  
  def initialize(): Unit = {
    window.initialize(680, 420, "Non-Sequitur")
    Input.initialize(window, step)
  }
  
  def mainloop(): Unit = {
    var currentTime = GLFW.glfwGetTime()
    var previousTime = currentTime
    while (!window.shouldClose()) {
      currentTime = GLFW.glfwGetTime()
      step.asInstanceOf[MutableSignal[Double]].set(Math.min(currentTime - previousTime, 0.1))
      previousTime = currentTime
      
      window.beginRendering()
      draw.asInstanceOf[MutableSignal[Unit]].set(())
      window.endRendering()
      
      Thread.sleep(5)
    }
  }
  
  def main(args: Array[String]): Unit = {
    initialize()
    
    val vao = createVertexArrayObject()
    val prgm = createProgram(
        createShader(new String(Resource.get("res.shader.simple-vertex")), GL_VERTEX_SHADER), 
        createShader(new String(Resource.get("res.shader.simple-fragment")), GL_FRAGMENT_SHADER))
    val data = Array[Double](
        -1, -1, 0,
        1, -1, 0,
        0, 1, 0)
    val vbo = createBufferObject(data, GL_STATIC_DRAW)
    
    draw.subscribe(() => drawSimpleTriangles(vbo, prgm))
    
    mainloop()
  }
}