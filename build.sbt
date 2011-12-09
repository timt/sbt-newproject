sbtPlugin := true

organization := "org.tbag"

name := "sbt-newproject"

version := "0.1"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "1.6.1",
    "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test"
)

publishTo <<= (version) { version: String =>
  val github = "/Users/timt/Projects/timt.github.com/maven/"
  if (version.trim.endsWith("SNAPSHOT")) Some(Resolver.file("file",  new File( github + "snapshots/")))
  else                                   Some(Resolver.file("file",  new File( github + "releases/")))
}

crossScalaVersions := Seq("2.9.1")

