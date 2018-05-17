package com.htmlism

import scala.sys.process._

import cats.effect._
import cats.implicits._

import better.files.File

class DisassemblerRunner[F[_]](implicit F: Sync[F]) {
  def findClassFiles(root: File): F[List[File]] =
    F.delay {
      root
        .listRecursively
        .filter(f => f.extension.contains(".class"))
        .toList
        .sortBy(_.pathAsString)
    }

  def disassemble(opt: DisassemblyOptions, fs: List[File]): F[Unit] =
    fs
      .traverse(disassemble(opt))
      .void

  def disassemble(opt: DisassemblyOptions)(f: File): F[Unit] =
    F.delay {
      println(("javap" +: opt.flags :+ f.pathAsString).!!)
    }
}