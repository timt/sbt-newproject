package org.tbag

import sbt._
import java.lang.String
import java.io.File

class BuildSbtFile(baseDir: File, currentRef: ProjectRef, scope: Settings[Scope], otherLibDependencies: Seq[String] = Seq()) {

  def write(settingKeys: SettingKey[String]*) = {
    assertDoesNotExist()
    val newBuildSettings = (settingKeys map {
      (settingKey: SettingKey[String]) => (settingKey, getNewValueFromUserFor(settingKey))
    }).toMap

    val content = (settingKeys map {
      (settingKey: SettingKey[String]) => """%s := "%s"""".format(settingName(settingKey), newBuildSettings.get(settingKey).getOrElse(""))
    } mkString "\n\n") + libDependenciesBuildSbtString

    IO.write(new File(baseDir, "build.sbt"), content)
    println("Generated build file")
    newBuildSettings
  }

  private def settingName(settingKey: SettingKey[String]) = {
    settingKey.key.label match {
      case "scala-version" => "scalaVersion"
      case s => s
    }
  }

  private def assertDoesNotExist() {
    val buildSbtFile: File = new File(baseDir, "build.sbt")
    if (buildSbtFile.exists()) sys.error(
      "\nbuild.sbt file already exists for this project at path %s" format buildSbtFile.getParent
    )
  }

  private def currentValueFor(settingKey: SettingKey[String]): String = {
    (settingKey in currentRef get scope).getOrElse("")
  }

  private def getNewValueFromUserFor(settingKey: SettingKey[String]): String = {
    val currentValue: String = currentValueFor(settingKey)
    SimpleReader.readLine(settingKey.key + "[" + currentValue + "]: ") match {
      case Some("") => currentValue
      case Some(s) => s
      case None => currentValue
    }
  }

  val libraryDependencies = otherLibDependencies ++ Seq(
    """"org.scalatest" %% "scalatest" % "2.1.6" % "test""""
  )

  val libDependenciesBuildSbtString =
    """
      |
      |libraryDependencies ++= Seq(
    %s
      |)""".format((libraryDependencies map ("|    " + _)).mkString(",\n")).stripMargin
}

object BuildSbtFile {
  def simpleBuildSbtFile(sbtStuff: SbtStuff) = {
    new BuildSbtFile(sbtStuff.baseDir, sbtStuff.projectRef, sbtStuff.scopeSettings)
  }

  def webBuildSbtFile(sbtStuff: SbtStuff) = {
    val unfilteredLibs = Seq(
      """"net.databinder" %% "unfiltered-filter" % "0.7.1" % "compile"""",
      """"net.databinder" %% "unfiltered-jetty" % "0.7.1" % "compile"""",
      """"org.eclipse.jetty.orbit" % "javax.servlet" % "2.5.0.v201103041518" % "compile""""
    )
    new BuildSbtFile(sbtStuff.baseDir, sbtStuff.projectRef, sbtStuff.scopeSettings, unfilteredLibs)
  }

  def liftwebBuildSbtFile(sbtStuff: SbtStuff) = {
    val liftAndJettyLibs = Seq(
      """"net.liftweb" %% "lift-webkit" % "2.5" % "compile"""",
      """"org.eclipse.jetty" % "jetty-webapp" % "8.1.15.v20140411" % "compile""""
    )
    new BuildSbtFile(sbtStuff.baseDir, sbtStuff.projectRef, sbtStuff.scopeSettings, liftAndJettyLibs)
  }
}








