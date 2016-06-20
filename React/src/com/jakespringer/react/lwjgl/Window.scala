package com.jakespringer.react.lwjgl

import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR
import org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR
import org.lwjgl.glfw.GLFW.GLFW_DOUBLEBUFFER
import org.lwjgl.glfw.GLFW.GLFW_FALSE
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE
import org.lwjgl.glfw.GLFW.GLFW_RESIZABLE
import org.lwjgl.glfw.GLFW.GLFW_SAMPLES
import org.lwjgl.glfw.GLFW.GLFW_TRUE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDefaultWindowHints
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor
import org.lwjgl.glfw.GLFW.glfwGetVideoMode
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback
import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwSwapInterval
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.glfw.GLFWWindowSizeCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.system.MemoryUtil.NULL

class Window[R <: Renderer](val renderer: R) {
  private var window: Long = 0

  def initialize(width: Int, height: Int, title: String): Unit = {
    GLFWErrorCallback.createPrint(System.err).set()
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW")
    }

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
    glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE)
    glfwWindowHint(GLFW_SAMPLES, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    window = glfwCreateWindow(width, height, title, NULL, NULL)
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window")
    }

    glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallbackI() {
      override def invoke(a: Long, b: Int, c: Int): Unit = {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClearColor(0, 0, 0, 1)
        glfwSwapBuffers(window)
        renderer.resize(b, c)
      }
    });

    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2)

    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwShowWindow(window)

    GL.createCapabilities()

    renderer.resize(width, height)
    renderer.beginRendering()
    renderer.endRendering()
  }

  def destroy(): Unit = {
    Callbacks.glfwFreeCallbacks(window)
    glfwDestroyWindow(window)
    glfwTerminate()
  }

  def setKeyCallback(cb: Function5[Long, Int, Int, Int, Int, Unit]): Unit = {
    glfwSetKeyCallback(window, new GLFWKeyCallbackI() {
      override def invoke(a: Long, b: Int, c: Int, d: Int, e: Int): Unit = {
        cb(a, b, c, d, e)
      }
    })
  }

  def setShouldClosed(closed: Boolean): Unit = {
    glfwSetWindowShouldClose(window, closed)
  }

  def shouldClose(): Boolean = {
    glfwWindowShouldClose(window)
  }

  def beginRendering(): Unit = {
    renderer.beginRendering()
  }

  def endRendering(): Unit = {
    renderer.endRendering()
    glfwSwapBuffers(window)
    glfwPollEvents()
  }
}