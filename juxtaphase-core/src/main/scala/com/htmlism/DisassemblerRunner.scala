package com.htmlism

import scala.sys.process._

import cats.effect._
import cats.implicits._

import better.files.File

class DisassemblerRunner[F[_]](implicit F: Sync[F]) {
  def findClassFiles(proj: SbtProject): F[List[File]] =
    F.delay {
      proj
        .targetDir
        .listRecursively
        .filter(f => f.extension.contains(".class"))
        .toList
        .sortBy(_.pathAsString)
    }

  def disassemble(opt: DisassemblyOptions)(fs: List[File]): F[Unit] =
    fs
      .traverse(disassembleEach(opt))
      .void

  private def disassembleEach(opt: DisassemblyOptions)(f: File): F[Unit] =
    F.delay {
      println(("javap" +: opt.flags :+ f.pathAsString).!!)
    }
}