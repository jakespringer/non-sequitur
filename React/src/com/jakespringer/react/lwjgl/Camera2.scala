package com.jakespringer.react.lwjgl

import org.lwjgl.opengl.GL11._
import com.jakespringer.react.util.Vec2

object Camera2 {
  def calculateViewport(aspectRatio: Double, w: Int, h: Int) {
    var vw: Int = 0
    var vh: Int = 0
    if (w > h * aspectRatio) {
      vh = h
      vw = (h * aspectRatio).toInt
    } else {
      vw = w
      vh = (w / aspectRatio).toInt
    }
    val left = (w - vw) / 2
    val bottom = (h - vh) / 2
    glViewport(left, bottom, vw, vh)
  }

  def getViewportSize(aspectRatio: Double, w: Int, h: Int): (Int, Int) = {
    if (w > h * aspectRatio) {
      ((h * aspectRatio).toInt, h)
    } else {
      (w, (w / aspectRatio).toInt)
    }
  }

  def setProjection(LL: Vec2, UR: Vec2) {
//    glMatrixMode(GL_PROJECTION)
//    glLoadIdentity()
//    glOrtho(LL.x, UR.x, LL.y, UR.y, -1, 1)
//    glMatrixMode(GL_MODELVIEW)
//    glLoadIdentity()
//    glDisable(GL_ALPHA_TEST)
//    glDisable(GL_LIGHTING)
//    glDisable(GL_DEPTH_TEST)
  }
}
