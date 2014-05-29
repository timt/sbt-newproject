package org.tbag

import sbt._
import Keys._
import BuildSbtFile._
import SbtStuff._
import collection.Seq

object NewProjectPlugin extends Plugin {
  override lazy val settings = Seq(
    commands ++= Seq(genSimpleProjectCommand, genWebProjectCommand, genLiftwebProjectCommand)
  )

  lazy val genSimpleProjectCommand =
    Command.command("gen-simple-project") {
      (state: State) =>
        val newBuildSettings =
          simpleBuildSbtFile(state)
            .write(name, organization, version, scalaVersion)

        //        Project.runTask(compile in Compile, state)
        val source: Source = new Source(SbtStuff(state).baseDir, newBuildSettings.get(organization).getOrElse("default"))
        source.createSrcDirs()
        source.createSimpleSampleCode()
        println("Finnised creating project, now run test/run to verify, and start hacking on the project")
        state.reload
    }

  lazy val genWebProjectCommand =
    Command.command("gen-web-project") {
      (state: State) =>
        val newBuildSettings = webBuildSbtFile(state)
          .write(name, organization, version, scalaVersion)
        val source: Source = new Source(SbtStuff(state).baseDir, newBuildSettings.get(organization).getOrElse("default"))
        source.createSrcDirs()
        source.createWebSampleCode()
        println("Created simple webapp, now type ~run to start it, and start hacking on HelloWorldWebApp.scala")
        state.reload
    }

  lazy val genLiftwebProjectCommand =
    Command.command("gen-lift-project") {
      (state: State) =>
        val newBuildSettings = liftwebBuildSbtFile(state)
          .write(name, organization, version, scalaVersion)
        val source: LiftwebSource = new LiftwebSource(SbtStuff(state).baseDir, newBuildSettings.get(organization).getOrElse("default"))
        source.createSrcDirs()
        source.createSourceFiles()
        println("Created simple liftweb app, now type run to start it, and start hacking on the project")
        state.reload
    }
}