package com.htmlism

import scala.sys.process._

import cats.effect._
import better.files.File

object Main extends RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit] =
    withEnvReader { env =>
      withRunnersIo { (cpl, dis) =>
        for {
          src <- env.getSourceFile(args)
          opt <- env.detectDisassemblyOptions
          tmp <- cpl.createTempDirectory
            _ <- cpl.runCompiler(src, tmp)
           cs <- dis.findClassFiles(tmp)
        } yield disassemble(cs, opt)
      }
    }

  def disassemble(files: Seq[File], opt: DisassemblyOptions) = {
    for (f <- files) {
      println
      println(f.pathAsString)

      println(("javap" +: opt.flags :+ f.pathAsString).!!)
    }
  }

  private def withEnvReader[A](f: EnvironmentReader[IO] => IO[A]) =
    f(new EnvironmentReader[IO])
}
