package org.tbag

import sbt._
import collection.Seq
import java.lang.String
import java.io.File

class Source(baseDir: File, org: String) {

  def createSrcDirs() {
    IO.createDirectories(srcDirs())
    println("Generated src folders")
  }

  def createSimpleSampleCode() {
    createHelloWorldSource()
    createHelloWorldSpec()
  }

  def createWebSampleCode() {
    createHelloWorldWebSource()
    //    createHelloWorldSpec
  }

  private def createHelloWorldSource() {
    IO.write(new File(srcDirWithOrgPackage("main", "scala"), "HelloWorld.scala"), helloWorldContent)
    println("Generated sample HelloWorld class")
  }

  private def createHelloWorldSpec() {
    IO.write(new File(srcDirWithOrgPackage("test", "scala"), "HelloWorldSpec.scala"), helloWorldSpecContent)
    println("Generated sample HelloWorld spec")
  }

  def createHelloWorldWebSource() {
    IO.write(new File(srcDirWithOrgPackage("main", "scala"), "HelloWorldWebApp.scala"), helloWorldWebAppContent)
    println("Generated sample HelloWorld webapp")
  }


  private def srcDirs(): Seq[File] =
    dirs("main") ++ dirs("test")

  private def dirs(rootDirName: String): Seq[File] =
    new File(baseDir, srcDirWithOrgPackage(rootDirName, "scala")) :: new File(baseDir, srcDir(rootDirName, "resources")) :: Nil


  private def srcDir(rootDirName: String, leafDirName: String) =
    s"src/$rootDirName/$leafDirName"

  private def srcDirWithOrgPackage(rootDirName: String, leafDirName: String) =
    s"${srcDir(rootDirName, leafDirName)}/${org.replace(".", "/")}"

  private val helloWorldContent =
    """|package %s
    |
    |class HelloWorld {
    |  def greeting:String = {
    |    return "Hello World"
    |  }
    |}
    |
    |object HelloWorld extends App{
    |  println(new HelloWorld().greeting)
    |}""".stripMargin.format(org)

  private val helloWorldWebAppContent =
    """|package %s
    |
    |object HelloWorldWebApp extends App{
    |  import unfiltered.request._
    |  import unfiltered.response._
    |  val echo = unfiltered.filter.Planify {
    |    case Path(Seg(p :: Nil)) => ResponseString(p)
    |    case _ => ResponseString("Hello World!")
    |  }
    |  unfiltered.jetty.Http.anylocal.filter(echo).run()
    |}""".stripMargin.format(org)

  private val helloWorldSpecContent =
    """|package %s
      |
      |import org.scalatest.Spec
      |import org.scalatest.Matchers._
      |
      |class HelloWorldSpec extends Spec {
      |
      |  def `HelloWorld should return 'Hello World'` {
      |      new HelloWorld().greeting should be ("Hello World")
      |  }
      |}
    """.stripMargin.format(org)
}



