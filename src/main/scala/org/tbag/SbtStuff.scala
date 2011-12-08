package org.tbag

import sbt._

object SbtStuff {
  implicit def toSbtStuff(state: State) = {
    new SbtStuff(state)
  }
  def apply(state: State)={
    toSbtStuff(state)
  }
}

class SbtStuff(state: State) {
  val extracted: Extracted = Project.extract(state)
  import state._
  import extracted._
  def baseDir = configuration.baseDirectory
  def projectRef = currentRef
  def scopeSettings = structure.data
}










