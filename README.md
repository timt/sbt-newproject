**sbt-newproject** is a plugin for simple-build-tool that create simple project outlines for sbt projects

Requirements
------------

* [sbt](https://github.com/harrah/xsbt/wiki) 0.11.2


Installation
------------

**sbt 0.11.2:**

Add the following lines to ~/.sbt/plugins/build.sbt or PROJECT_DIR/project/plugins.sbt

    resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

    addSbtPlugin("org.tbag" % "sbt-newproject" % "0.1")

Usage
-----
Create a simple project

`gen-simple-project` sbt task to create simple project files.

Create an unfiltered web project

`gen-web-project` sbt task to create unfiltered web project files.

License
-------

Licensed under the New BSD License. See the LICENSE file for details.
