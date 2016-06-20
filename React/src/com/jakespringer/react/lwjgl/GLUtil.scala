package com.jakespringer.react.lwjgl

import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._

object GLUtil {
  def createVertexArrayObject(): Int = {
    val array = glGenVertexArrays()
    glBindVertexArray(array)
    array
  }

  def createBufferObject(data: Array[Double], dataTypeHint: Int = GL_STATIC_DRAW): Int = {
    val vbo = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER, data, dataTypeHint)
    vbo
  }

  def createTexture(data: Array[Double], width: Int, height: Int, internalDataRepresentation: Int): Int = {
    val tex = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, tex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, internalDataRepresentation, GL_UNSIGNED_BYTE, data);
    tex
  }

  def createShader(src: String, shaderType: Int): Int = {
    val shader = glCreateShader(shaderType)
    glShaderSource(shader, src)
    glCompileShader(shader)

    val compileStatus = new Array[Int](1)
    glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus);

    if (compileStatus(0) == 0) {
      throw new IllegalArgumentException("Shader could not compile correctly: " + glGetShaderInfoLog(shader))
    }

    shader
  }

  def createProgram(vertexShader: Int, fragmentShader: Int): Int = {
    val program = glCreateProgram()
    glAttachShader(program, vertexShader)
    glAttachShader(program, fragmentShader)
    glLinkProgram(program)

    val linkResult = new Array[Int](1)
    glGetProgramiv(program, GL_LINK_STATUS, linkResult)

    if (linkResult(0) == 0) {
      throw new IllegalArgumentException("Program could not be linked: " + glGetProgramInfoLog(program))
    }

    glDetachShader(program, vertexShader)
    glDetachShader(program, fragmentShader)

    glDeleteShader(vertexShader)
    glDeleteShader(fragmentShader)

    program
  }

  def destroyProgram(programId: Int): Unit = {
    glDeleteProgram(programId)
  }

  def destroyVertexArrayObject(vaoId: Int): Unit = {
    glDeleteVertexArrays(vaoId)
  }

  def destroyBufferObject(vboId: Int): Unit = {
    glDeleteBuffers(vboId)
  }
  
  def destroyTexture(tex: Int): Unit = {
    glDeleteTextures(tex)
  }
  
  def drawSimpleTriangles(vboId: Int, programId: Int): Unit = {
    glUseProgram(programId);
    
		glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glVertexAttribPointer(
			0,
			3,
			GL_DOUBLE,
			false,
			0,
			0);

		glDrawArrays(GL_TRIANGLES, 0, 3);

		glDisableVertexAttribArray(0);
  }
}