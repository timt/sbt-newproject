sbtPlugin := true

organization := "org.tbag"

name := "sbt-newproject"

version := "0.4"

scalaVersion := "2.10.4"

publishTo <<= version { version: String =>
  val github = "/Users/timt/Projects/timt.github.com/repo/"
  if (version.trim.endsWith("SNAPSHOT")) Some(Resolver.file("file",  new File( github + "snapshots/")))
  else                                   Some(Resolver.file("file",  new File( github + "releases/")))
}

crossScalaVersions := Seq("2.11.0")

