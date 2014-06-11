package org.tbag

import sbt.IO
import java.io.File


object WriteFile {
  def apply(srcDir: String, filename: String, content: String) {
    IO.write(new File(srcDir, filename), content)
    println(s"Generated file => $srcDir/$filename")
  }
}