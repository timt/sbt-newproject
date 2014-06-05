package org.tbag

import java.lang.String
import java.io.File

class LiftwebSource(baseDir: File, name: String, org: String) extends Source(baseDir, org) {
  def createSourceFiles() {
    WriteFile(srcMainResourcesDir("props"), "default.props", defaultProps)
    WriteFile(srcMainResourcesDir(""), "logback.xml", logback)

    WriteFile(srcMainScalaDir("bootstrap/liftweb"), "Boot.scala", liftBootContent)
    WriteFile(srcDir("main", "scala"), "WebServer.scala", jettyWebServer)
    WriteFile(srcDir("main", "scala"), "WebServerApp.scala", webServerApp)
    WriteFile(srcDir("main", "scala"), "RestService.scala", restService)
    WriteFile(srcWebappDir("WEB-INF"), "web.xml", webXml)
    WriteFile(srcWebappDir(""), "index.html", indexHtml)

    WriteFile("project", "SbtBuild.scala", sbtBuildFileContents(name))
    WriteFile("distribution", "run.sh", runScript)
  }

  private def srcDir(rootDirName: String, leafDirName: String) = s"src/$rootDirName/$leafDirName/${org.replace(".", "/")}"

  private def srcMainScalaDir(leafDirName: String) = s"src/main/scala/$leafDirName"

  private def srcMainResourcesDir(leafDirName: String) = s"src/main/resources/$leafDirName"

  private def srcWebappDir(leafDirName: String) = s"src/main/webapp/$leafDirName"

  private val defaultProps = """""".stripMargin

  private val logback =
    """<configuration>
      |    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
      |        <file>logs/app.log</file>
      |        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      |            <!--daily-->
      |            <fileNamePattern>app.%d{yyyy-MM-dd}.log.gz</fileNamePattern>
      |            <maxHistory>90</maxHistory>
      |        </rollingPolicy>
      |        <encoder>
      |            <pattern>%date %level [%thread] [%file:%line] %msg%n</pattern>
      |        </encoder>
      |    </appender>
      |
      |    <logger name="org.eclipse.jetty" level="INFO"/>
      |
      |    <logger name="bootstrap.liftweb" level="INFO"/>
      |
      |    <root level="warn">
      |        <appender-ref ref="FILE"/>
      |    </root>
      |</configuration>""".stripMargin

  private val liftBootContent =
    """|package bootstrap.liftweb
      |
      |import net.liftweb.common.Loggable
      |import net.liftweb.http.LiftRules
      |import default.RestService
      |
      |class Boot extends Loggable {
      |  def boot() {
      |    LiftRules.addToPackages("%s")
      |    System.setProperty("run.mode", "production")
      |
      |    LiftRules.statelessDispatch.append(RestService)
      |
      |    println("### Lift has booted.")
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

  private val indexHtml =
    """<!DOCTYPE html>
      |<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
      |<head>
      |    <meta charset="UTF-8"/>
      |    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
      |</head>
      |<body>
      |    <h1>It works!</h1>
      |</body>
      |</html>""".stripMargin

  val runScript =
    """#!/bin/sh
      |
      |APP_HOME=`dirname $0`
      |COMMAND=$1
      |
      |function start() {
      |    echo "TODO Implement start!"
      |}
      |
      |function stop() {
      |    echo "TODO Implement stop!"
      |}
      |
      |function restart() {
      |    echo "TODO Implement restart!"
      |}
      |
      |function status() {
      |    echo "TODO Implement status!"
      |}
      |
      |case "${COMMAND}" in
      |    'start') start ;;
      |    'stop') stop ;;
      |    'restart') restart ;;
      |    'status') status ;;
      |    *) echo "Usage ${0} [ start | stop | restart | status ]" ;;
      |
      |esac
      |
      |exit 0""".stripMargin

  def sbtBuildFileContents(name: String) =
    """import java.lang.System
      |import sbt._
      |import Keys._
      |import sbt.IO._
      |import scala.Predef._
      |
      |
      |object BuildSettings {
      |  System.setProperty("run.mode", "production")
      |
      |  val dist = TaskKey[Unit]("dist")
      |
      |  val myBuildSettings: Seq[Setting[_]] = Defaults.defaultSettings ++ Seq[Setting[_]](
      |    scalacOptions := Seq( "-feature", "-language:_", "-deprecation", "-unchecked"),
      |    parallelExecution := true,
      |    parallelExecution in Test := false,
      |    logBuffered := true,
      |    javaOptions ++= Seq("-Xmx1G", "-Xss4m", "-server"),
      |    (testOptions in Test) <+= (target in Test) map {
      |      t ⇒ Tests.Argument("-o", "-u", "test-reports")
      |    },
      |    dist <<= (baseDirectory, target, packageBin in Compile, dependencyClasspath in Compile) map {
      |      (theBase, targetDir, artifact, classpath) ⇒
      |        val jarZipEntries = classpath.map(_.data) pair flatRebase("lib/")
      |        val files = Seq(
      |          artifact -> "lib/%s.jar",
      |          theBase / "distribution" / "run.sh" -> "run.sh"
      |        )
      |        zip(jarZipEntries ++ files, targetDir / "dist.zip")
      |    }
      |  )
      |}
      |
      |object SbtBuild extends Build {
      |  import BuildSettings._
      |  import sbt._
      |
      |  lazy val myProject = Project("%s", file(".")).settings(myBuildSettings: _*)
      |}""".stripMargin.format(name, name)

  val restService =
    """package default
      |
      |import net.liftweb.http.rest.RestHelper
      |import net.liftweb.common.Loggable
      |import net.liftweb.http.InMemoryResponse
      |import scala.collection.immutable.::
      |
      |
      |object RestService extends RestHelper with Loggable {
      |  serve {
      |    case r @ "hello" :: _ Get _ ⇒ InMemoryResponse("Hello to you too!".getBytes, Nil, Nil, 200)
      |  }
      |}""".stripMargin
}