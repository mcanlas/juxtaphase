package com.htmlism

import java.nio.file.attribute.PosixFilePermission

import scala.sys.process._

import cats.effect._
import cats.implicits._

import better.files.File
import better.files.Dsl._

class CompilerRunner[F[_]](implicit F: Sync[F]) {
  private def createTempDirectory: F[SbtProject] =
    F.delay {
      new SbtProject
    }

  private def createSrcDirectory(proj: SbtProject): F[File] =
    F.delay {
      proj.scalaDir.createIfNotExists(asDirectory = true)
    }

  private def createBuildFile(proj: SbtProject): F[File] =
    F.delay {
      proj
        .buildFile
        .createIfNotExists()
        .appendLine("scalaVersion := \"2.12.6\"")
        .appendLine("scalacOptions += \"-Xprint:1\"")
    }

  private val allPhases = (1 to 2).mkString(",")

  def runCompiler(srcFile: String, tmpDir: File): F[Unit] =
    F.delay {
      println(Seq("scalac", "-Xprint:" + allPhases, "-d", tmpDir.pathAsString, srcFile).!!)
    }

  def runCompilerWithSbt(src: String): F[SbtProject] =
    createTempDirectory
      .flatTap(r => createSrcDirectory(r) >>= copySourceIntoScalaDirectory(src))
      .flatTap(createBuildFile)
      .flatTap(r => createSbtRunner(r) >>= makeExecutable)
      .flatTap(runCompilerWithSbt)

  private def copySourceIntoScalaDirectory(src: String)(scalaDir: File): F[Unit] =
    F.delay {
      File(src).copyToDirectory(scalaDir)
    }

  private def createSbtRunner(proj: SbtProject): F[File] =
    F.delay {
      proj
        .sbtRunner
        .createIfNotExists()
        .appendLine("#!/usr/bin/env bash")
        .appendLine(s"cd ${proj.path}")
        .appendLine("sbt test")
    }

  private def makeExecutable(f: File): F[Unit] =
    F.delay {
      chmod_+(PosixFilePermission.OWNER_EXECUTE, f)
    }

  private def runCompilerWithSbt(proj: SbtProject): F[Unit] =
    F.delay {
      val file = proj.sbtRunner.pathAsString

      println(Seq(file).!!)
    }
}
