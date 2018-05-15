package com.htmlism

import cats.effect._

trait RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit]

  def withRunnerIo(f: CompilerRunner[IO] => IO[Unit]): IO[Unit] =
    f(new CompilerRunner[IO])

  def main(args: Array[String]): Unit =
    mainIo(args).unsafeRunSync()
}
