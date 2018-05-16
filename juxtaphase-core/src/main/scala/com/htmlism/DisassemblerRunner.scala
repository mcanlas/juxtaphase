package com.htmlism

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
}