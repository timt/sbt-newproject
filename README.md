**sbt-newproject** is a plugin for simple-build-tool that create simple project outlines for sbt projects

Generates a simple build.sbt file and creates the project structure:

    src/
      main/
        resources/
           <files to include in main jar here>
        scala/
           <main Scala sources>
      test/
        resources
           <files to include in test jar here>
        scala/
           <test Scala sources>

Requirements
------------

* [sbt](https://github.com/harrah/xsbt/wiki) 0.13.2


Installation
------------

**sbt 0.13.2:**

Add the following lines to ~/.sbt/plugins/build.sbt or PROJECT_DIR/project/plugins.sbt

    resolvers += "sbt-newproject-repo" at "http://timt.github.com/repo/releases/"

    addSbtPlugin("org.tbag" % "sbt-newproject" % "0.4")

Usage
-----
Create a simple project

    sbt gen-simple-project
    ...
    sbt test
    sbt run

Create an unfiltered web project

    sbt gen-web-project
    ...
    sbt test
    sbt run

Create an liftweb project

    sbt gen-lift-project
    ...
    sbt test
    sbt run

License
-------

Licensed under the New BSD License. See the LICENSE file for details.
