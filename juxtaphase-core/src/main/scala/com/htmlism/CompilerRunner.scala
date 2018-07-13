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

  private def createSrcDirectory(proj: SbtProject): F[File] =
    F.delay {
      proj.scalaDir.createIfNotExists(asDirectory = true)
    }

  private def createBuildFile(opt: CompilerOptions)(proj: SbtProject): F[File] =
    F.delay {
      proj
        .buildFile
        .createIfNotExists()
        .appendLine("scalaVersion := \"" + opt.scalaVersion + "\"")
        .appendLine("scalacOptions += \"-Xprint:" + opt.phases.mkString(",") + "\"")
    }

  def runCompilerWithSbt(opt: CompilerOptions)(src: String): F[SbtProject] =
    createTempDirectory
      .map { f => println(f); f }
      .map(new SbtProject(_))
      .flatTap(createSrcDirectory(_) >>= copySourceIntoScalaDirectory(src))
      .flatTap(createBuildFile(opt))
      .flatTap(createSbtRunner(_) >>= makeExecutable)
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
