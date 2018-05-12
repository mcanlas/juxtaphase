package com.htmlism

import scala.sys.process._

import cats.effect._
import better.files.File

object Main {
  def main(args: Array[String]): Unit =
    args.lift(0) match {
      case Some(s) =>
        compile(s)

      case None =>
        throw new IllegalArgumentException("need to specify a file")
    }

  def compile(s: String) = {
    val allPhases = (1 to 24).mkString(",")

    val ior = new Runner[IO]

    val prog =
      for {
          d <- ior.createTempDirectory
        src <- ior.createSrcDirectory(d)
          _ <- ior.createBuildFlie(d)
      } yield { println(src) }

    prog.unsafeRunSync()

    val tmpDir = File.newTemporaryDirectory()

    println(Seq("scalac", "-Xprint:" + allPhases, "-d", tmpDir.pathAsString, s).!!)

    println(tmpDir)

    val files = tmpDir
      .listRecursively
      .filter(f => f.extension.contains(".class"))
      .toList
      .sortBy(_.pathAsString)

    val opt = CommandLineFlags.detectDisassemblyOptions

    for (f <- files) {
      println
      println(f.pathAsString)

      println(("javap" +: opt.flags :+ f.pathAsString).!!)
    }
  }
}
