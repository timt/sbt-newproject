sbtPlugin := true

organization := "org.tbag"

name := "sbt-newproject"

version := "0.2"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "1.12.4",
    "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test"
)

publishTo <<= (version) { version: String =>
  val github = "/Users/timt/Projects/timt.github.com/repo/"
  if (version.trim.endsWith("SNAPSHOT")) Some(Resolver.file("file",  new File( github + "snapshots/")))
  else                                   Some(Resolver.file("file",  new File( github + "releases/")))
}

crossScalaVersions := Seq("2.9.2")

