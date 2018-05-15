package com.htmlism

import scala.sys.process._

import cats.effect._
import better.files.File

object Main extends RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit] =
    withEnvReader { env =>
      withRunnerIo { cpl =>
        for {
          src <- env.getSourceFile(args)
          opt <- env.detectDisassemblyOptions
          tmp <- cpl.createTempDirectory
            _ <- cpl.runCompiler(src, tmp)
        } yield disassemble(tmp, opt)
      }
    }

  def disassemble(tmpDir: File, opt: DisassemblyOptions) = {
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
