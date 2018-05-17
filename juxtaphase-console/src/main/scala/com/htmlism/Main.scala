package com.htmlism

import cats.effect._

object Main extends RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit] =
    withEnvReader { env =>
      withRunnersIo { (cpl, dis) =>
        for {
          src <- env.getSourceFile(args)
          opt <- env.detectDisassemblyOptions

          tmp <- cpl.runCompilerWithSbt(src)
          _ = println(tmp)

            _ <- cpl.runCompiler(src, tmp)
           cs <- dis.findClassFiles(tmp)
            _ <- dis.disassemble(opt, cs)
        } yield ()
      }
    }

  private def withEnvReader[A](f: EnvironmentReader[IO] => IO[A]) =
    f(new EnvironmentReader[IO])
}
