package com.htmlism

import scala.sys.process._

import cats.effect._
import better.files.File

object Main extends RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit] =
    withEnvReader { env =>
      withRunnerIo { _ =>
        for {
          src <- env.getSourceFile(args)
          opt <- env.detectDisassemblyOptions
        } yield compile(src, opt)
      }
    }

  def compile(s: String, opt: DisassemblyOptions) = {
    val allPhases = (1 to 24).mkString(",")

    withRunnerIo { io =>
      for {
          d <- io.createTempDirectory
        src <- io.createSrcDirectory(d)
          _ <- io.createBuildFlie(d)
      } yield { println(src) }
    }

    val tmpDir = File.newTemporaryDirectory()

    println(Seq("scalac", "-Xprint:" + allPhases, "-d", tmpDir.pathAsString, s).!!)

    println(tmpDir)

    val files = tmpDir
      .listRecursively
      .filter(f => f.extension.contains(".class"))
      .toList
      .sortBy(_.pathAsString)

    for (f <- files) {
      println
      println(f.pathAsString)

      println(("javap" +: opt.flags :+ f.pathAsString).!!)
    }
  }

  private def withEnvReader[A](f: EnvironmentReader[IO] => IO[A]) =
    f(new EnvironmentReader[IO])
}
