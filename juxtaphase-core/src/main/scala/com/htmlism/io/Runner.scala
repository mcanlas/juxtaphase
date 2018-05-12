package com.htmlism.io

import cats.effect._

import better.files.File

class Runner[F[_]](implicit F: Sync[F]) {
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
}
