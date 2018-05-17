package com.htmlism

import java.nio.file.attribute.PosixFilePermission

import scala.sys.process._

import cats.effect._
import cats.implicits._

import better.files.File
import better.files.Dsl._

class CompilerRunner[F[_]](implicit F: Sync[F]) {
  private def createTempDirectory: F[File] =
    F.delay {
      File.newTemporaryDirectory()
    }

  private def createSrcDirectory(root: File): F[File] =
    F.delay {
      (root / "src" / "main" / "scala").createIfNotExists(asDirectory = true)
    }

  private def createBuildFile(sbtRoot: File): F[File] =
    F.delay {
      (sbtRoot / "build.sbt")
        .createIfNotExists()
        .appendLine("scalaVersion := \"2.12.6\"")
        .appendLine("scalacOptions += \"-Xprint:1\"")
    }

  private val allPhases = (1 to 2).mkString(",")

  def runCompiler(srcFile: String, tmpDir: File): F[Unit] =
    F.delay {
      println(Seq("scalac", "-Xprint:" + allPhases, "-d", tmpDir.pathAsString, srcFile).!!)
    }

  def runCompilerWithSbt(src: String): F[File] =
    createTempDirectory
      .flatTap(r => createSrcDirectory(r) flatTap copySourceIntoScalaDirectory(src))
      .flatTap(createBuildFile)
      .flatTap(r => createSbtRunner(r) flatTap makeExecutable)
      .flatTap(runCompilerWithSbt)

  private def copySourceIntoScalaDirectory(src: String)(scalaDir: File): F[Unit] =
    F.delay {
      File(src).copyToDirectory(scalaDir)
    }

  private def createSbtRunner(sbtRoot: File): F[File] =
    F.delay {
      (sbtRoot / "sbt-runner.sh")
        .createIfNotExists()
        .appendLine("#!/usr/bin/env bash")
        .appendLine(s"cd ${sbtRoot.toString}")
        .appendLine("sbt test")
    }

  private def makeExecutable(f: File): F[Unit] =
    F.delay {
      chmod_+(PosixFilePermission.OWNER_EXECUTE, f)
    }

  private def runCompilerWithSbt(sbtRoot: File): F[Unit] =
    F.delay {
      val file = (sbtRoot / "sbt-runner.sh").toString

      println(Seq(file).!!)
    }
}
