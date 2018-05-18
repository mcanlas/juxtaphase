package com.htmlism

import cats.effect._
import cats.implicits._

object Main extends RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit] =
    withEnvReader { env =>
      withRunnersIo { (cpl, dis) =>
        for {
          src <- env.getSourceFile(args)
          opt <- env.detectDisassemblyOptions
            _ <- cpl.runCompilerWithSbt(src) >>= dis.findClassFiles >>= dis.disassemble(opt)
        } yield ()
      }
    }

  private def withEnvReader[A](f: EnvironmentReader[IO] => IO[A]) =
    f(new EnvironmentReader[IO])
}
