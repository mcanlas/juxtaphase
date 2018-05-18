package com.htmlism

import cats.effect._
import cats.implicits._

object Main extends RunUnsafeSync {
  def mainIo(args: Array[String]): IO[Unit] =
    mainSync[IO](args)

  def mainSync[F[_] : Sync](args: Array[String]): F[Unit] =
    for {
         env <- Sync[F].pure { new EnvironmentReader[F] }
         src <- env.getSourceFile(args)
      cmpOpt <- env.detectCompilerOptions
      disOpt <- env.detectDisassemblyOptions
         cpl =  new CompilerRunner[F]
         dis =  new DisassemblerRunner[F]
           _ <- cpl.runCompilerWithSbt(src) >>= dis.findClassFiles >>= dis.disassemble(disOpt)
    } yield ()
}
