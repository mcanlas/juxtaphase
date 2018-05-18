package com.htmlism

import better.files.File

class SbtProject {
  private val root = File.newTemporaryDirectory()

  lazy val path: String = root.pathAsString

  lazy val buildFile: File = root / "build.sbt"

  lazy val scalaDir: File = root / "src" / "main" / "scala"

  lazy val sbtRunner: File = root / "sbt-runner.sh"

  lazy val targetDir: File = root / "target"
}
