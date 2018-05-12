package com.htmlism

import cats.effect._

trait RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit]

  def withRunnerIo(f: Runner[IO] => IO[Unit]): IO[Unit] =
    f(new Runner[IO])

  def main(args: Array[String]): Unit =
    mainIo(args).unsafeRunSync()
}
