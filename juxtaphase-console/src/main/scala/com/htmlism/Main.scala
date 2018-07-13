package com.htmlism

import cats.effect._
import cats.implicits._

/**
 * {{{
 *   sbt "juxtaphase-console/run juxtaphase-core/src/main/scala/com/htmlism/CompilerOptions.scala"
 * }}}
 */
object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Pipeline
      .start[IO]
      .apply(args)
}

object Pipeline {
  def start[F[_] : Sync]: List[String] => F[ExitCode] =
    EnvironmentReader.getSourceFile _ andThen toIO

  private def toIO[F[_] : Sync](src: Option[String]) =
    src.fold(zero)(mainSync)

  private def zero[F[_]](implicit F: Sync[F]) =
    F
      .delay[Unit] { throw new IllegalArgumentException("need to specify a file") }
      .as(ExitCode.Error)

  private def mainSync[F[_] : Sync](src: String) =
    for {
      env <- Sync[F].pure { new EnvironmentReader[F] }
      cmpOpt <- env.detectCompilerOptions
      disOpt <- env.detectDisassemblyOptions
      cpl =  new CompilerRunner[F]
      dis =  new DisassemblerRunner[F]
      _ <- cpl.runCompilerWithSbt(cmpOpt)(src) >>= dis.findClassFiles >>= dis.disassemble(disOpt)
    } yield ExitCode.Success
}