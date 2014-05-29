package org.tbag

import sbt._
import java.lang.String
import java.io.File

class LiftwebSource(baseDir: File, org: String) extends Source(baseDir, org) {
  def createSourceFiles() {
    writeFile(srcMainScalaDir("bootstrap/liftweb"), "Boot.scala", liftBootContent)
    writeFile(srcDir("main", "scala"), "WebServer.scala", jettyWebServer)
    writeFile(srcDir("main", "scala"), "WebServerApp.scala", webServerApp)
    writeFile(srcWebappDir("WEB-INF"), "web.xml", webXml)
  }

  private def srcDir(rootDirName: String, leafDirName: String) = {
    "src/%s/%s/%s".format(rootDirName, leafDirName, org.replace(".", "/"))
  }

  private def srcMainScalaDir(leafDirName: String) = {
    "src/main/scala/%s".format(leafDirName)
  }

  private def srcWebappDir(leafDirName: String) = {
    "src/main/webapp/%s".format(leafDirName)
  }

  private def writeFile(srcDir: String, filename: String, content: String) {
    IO.write(new File(srcDir, filename), content)
    println(s"Generated file => $srcDir/$filename")
  }

  private val liftBootContent =
    """|package bootstrap.liftweb
    |
    |import net.liftweb.common.Loggable
    |
    |class Boot extends Loggable {
    |  def boot() {
    |    System.setProperty("run.mode", "production")
    |  }
    |}""".stripMargin.format(org)

  private val jettyWebServer =
  """package %s
    |
    |import org.eclipse.jetty.server.Server
    |import org.eclipse.jetty.server.nio.SelectChannelConnector
    |import org.eclipse.jetty.webapp.WebAppContext
    |import java.io.File
    |import java.lang.Runtime.getRuntime
    |
    |class WebServer(port: Int) {
    |  private val shutdownTimeout = 10000
    |  private val retryTimeout = 100
    |  private val server = buildServer(port)
    |  server.setHandler(createContext)
    |
    |  startServer()
    |
    |  private def startServer() {
    |    try {
    |      server.start()
    |      println(s"### Started web server on port $port...")
    |      while (!server.isRunning) Thread.sleep(retryTimeout)
    |    } catch {
    |      case e: Exception ⇒
    |        println(s"### Failed to start web server on port $port")
    |        e.printStackTrace()
    |        throw e
    |    }
    |  }
    |
    |  private def stopServer() {
    |    println(s"### Stopped web server on port $port...")
    |    server.stop()
    |    val end = System.currentTimeMillis() + shutdownTimeout
    |    while (!server.isStopped && end > System.currentTimeMillis()) Thread.sleep(retryTimeout)
    |    if (!server.isStopped) println("!!!!!!! SERVER FAILED TO STOP !!!!!!!")
    |  }
    |
    |  private def buildServer(port: Int) = {
    |    val server = new Server
    |    val scc = new SelectChannelConnector
    |    scc.setPort(port)
    |    server.setConnectors(Array(scc))
    |    server.setStopAtShutdown(true)
    |
    |    server
    |  }
    |
    |  private def createContext = {
    |    val context = new WebAppContext()
    |    context.setServer(server)
    |    context.setContextPath("/")
    |
    |    if (new File("src/main/webapp").exists())
    |      context.setWar("src/main/webapp")
    |    else
    |      context.setWar(getClass.getClassLoader.getResource("webapp").toExternalForm)
    |
    |    context
    |  }
    |
    |  getRuntime addShutdownHook new Thread {
    |    override def run() {
    |      stopServer()
    |    }
    |  }
    |}""".stripMargin.format(org)

  private val webServerApp =
  """package %s
    |
    |object WebServerApp extends App {
    |  new WebServer(8080)
    |}""".stripMargin.format(org)

  private val webXml =
  """<?xml version="1.0" encoding="ISO-8859-1"?>
    |<!DOCTYPE web-app PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN" "http://java.sun.com/dtd/web-app_2_3.dtd">
    |<web-app>
    |    <filter>
    |        <filter-name>LiftFilter</filter-name>
    |        <display-name>Lift Filter</display-name>
    |        <description>The Filter that intercepts lift calls</description>
    |        <filter-class>net.liftweb.http.LiftFilter</filter-class>
    |    </filter>
    |    <filter-mapping>
    |        <filter-name>LiftFilter</filter-name>
    |        <url-pattern>/*</url-pattern>
    |    </filter-mapping>
    |</web-app>""".stripMargin
}