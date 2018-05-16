package com.htmlism

import scala.sys.process._

import cats.effect._

import better.files.File

class DisassemblerRunner[F[_]](implicit F: Sync[F]) {
  def findClassFiles(root: File): F[Seq[File]] =
    F.delay {
      root
        .listRecursively
        .filter(f => f.extension.contains(".class"))
        .toList
        .sortBy(_.pathAsString)
    }

  def disassemble(opt: DisassemblyOptions, fs: Seq[File]): F[Unit] =
    F.delay {
      for (f <- fs)
        disassemble(opt, f)
    }

  private def disassemble(opt: DisassemblyOptions, f: File) =
    println(("javap" +: opt.flags :+ f.pathAsString).!!)
}