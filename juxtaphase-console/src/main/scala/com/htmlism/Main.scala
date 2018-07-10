package com.htmlism

import cats.effect._
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    mainSync[IO](args)
      .as(ExitCode.Success)

  def mainSync[F[_] : Sync](args: List[String]): F[Unit] =
    for {
         env <- Sync[F].pure { new EnvironmentReader[F] }
         src <- env.getSourceFile(args)
      cmpOpt <- env.detectCompilerOptions
      disOpt <- env.detectDisassemblyOptions
         cpl =  new CompilerRunner[F]
         dis =  new DisassemblerRunner[F]
           _ <- cpl.runCompilerWithSbt(cmpOpt)(src) >>= dis.findClassFiles >>= dis.disassemble(disOpt)
    } yield ()
}
