package com.htmlism

import scala.sys.process._

import cats.effect._

import better.files.File

class CompilerRunner[F[_]](implicit F: Sync[F]) {
  def createTempDirectory: F[File] =
    F.delay {
      File.newTemporaryDirectory()
    }

  def createSrcDirectory(root: File): F[File] =
    F.delay {
      (root / "src" / "main" / "scala").createIfNotExists(asDirectory = true)
    }

  def createBuildFlie(root: File): F[File] =
    F.delay {
      (root / "build.sbt")
        .createIfNotExists()
        .appendLine("scalaVersion := \"2.12.6\"")
    }

  private val allPhases = (1 to 24).mkString(",")

  def runCompiler(srcFile: String, tmpDir: File): F[Unit] =
    F.delay {
      println(Seq("scalac", "-Xprint:" + allPhases, "-d", tmpDir.pathAsString, srcFile).!!)
    }
}
