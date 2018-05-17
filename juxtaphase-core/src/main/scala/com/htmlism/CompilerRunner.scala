package com.htmlism

import scala.sys.process._

import cats.effect._
import cats.implicits._

import better.files.File

class CompilerRunner[F[_]](implicit F: Sync[F]) {
  private def createTempDirectory: F[File] =
    F.delay {
      File.newTemporaryDirectory()
    }

  private def createSrcDirectory(root: File): F[File] =
    F.delay {
      (root / "src" / "main" / "scala").createIfNotExists(asDirectory = true)
    }

  private def createBuildFile(root: File): F[File] =
    F.delay {
      (root / "build.sbt")
        .createIfNotExists()
        .appendLine("scalaVersion := \"2.12.6\"")
        .appendLine("scalacOptions += \"-Xprint:1\"")
    }

  private val allPhases = (1 to 24).mkString(",")

  def runCompiler(srcFile: String, tmpDir: File): F[Unit] =
    F.delay {
      println(Seq("scalac", "-Xprint:" + allPhases, "-d", tmpDir.pathAsString, srcFile).!!)
    }

  def runCompilerWithSbt(src: String): F[File] =
    for {
       sbtRoot <- createTempDirectory
      scalaDir <- createSrcDirectory(sbtRoot)
             _ <- copySourceIntoSclaDirectory(src, scalaDir)
      buildSbt <- createBuildFile(sbtRoot)
    } yield sbtRoot

  private def copySourceIntoSclaDirectory(src: String, scalaDir: File): F[Unit] =
    F.delay {
      File(src).copyToDirectory(scalaDir)
    }
}
