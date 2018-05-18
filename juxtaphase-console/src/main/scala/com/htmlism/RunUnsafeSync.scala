package com.htmlism

import cats.effect._

trait RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit]

  def main(args: Array[String]): Unit =
    mainIo(args).unsafeRunSync()
}
