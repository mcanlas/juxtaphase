package com.htmlism

import cats.effect._

trait RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit]

  def withRunnersIo(f: (CompilerRunner[IO], DisassemblerRunner[IO]) => IO[Unit]): IO[Unit] =
    f(new CompilerRunner, new DisassemblerRunner)

  def main(args: Array[String]): Unit =
    mainIo(args).unsafeRunSync()
}
