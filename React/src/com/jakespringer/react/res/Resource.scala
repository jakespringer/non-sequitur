package com.jakespringer.react.res

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Resource {
  private var rootDirectory = Paths.get(".")
  private var cacheMap = Map[String, (Array[Byte], Object)]()
  
  def getRootDirectory = rootDirectory
  
  def setRootDirectory(root: Path): Unit = {
    rootDirectory = root
  }
  
  def get(res: String, cache: Boolean=true): Array[Byte] = {
    if (cache) {
      if (cacheMap contains res) {
        cacheMap(res)._1
      } else {
        val bytes = Files.readAllBytes(getPathFromResource(res))
        cacheMap = cacheMap + (res -> (bytes, Nil))
        bytes
      }
    } else {
      Files.readAllBytes(getPathFromResource(res))
    }
  }
  
  def getPathFromResource(res: String): Path = {
    val tokens = res.split('.')
    val dir = Paths.get(rootDirectory.toString, tokens.dropRight(1): _*)
    val listOfFiles = dir.toFile().listFiles()
    val possibilities = listOfFiles.filter(x => {
      val filenameTokens = x.getName().split('.')
      if (filenameTokens.length == 1) {
        x.getName() == tokens.last
      } else {
        filenameTokens.dropRight(1).reduce((x: String, y: String) => x + y) == tokens.last
      }
    })
    
    if (possibilities.length != 1) throw new IllegalArgumentException("Invalid resource (either could not be found or ambiguous)")
    
    Paths.get(possibilities(0).getAbsolutePath())
  }
}