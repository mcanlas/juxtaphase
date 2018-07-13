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
    EnvironmentReader.getSourceFile _ andThen toIO[F]

  private def toIO[F[_] : Sync](src: Option[String]) =
    src.fold(zero)(mainSync[F])

  private def zero[F[_]](implicit F: Sync[F]) =
    F
      .delay { throw new IllegalArgumentException("need to specify a file") }
      .as(ExitCode.Error)

  private def mainSync[F[_] : Sync](src: String) = {
    val env = new EnvironmentReader

    val cmpOpt = env.detectCompilerOptions
    val disOpt = env.detectDisassemblyOptions

    val cpl = new CompilerRunner[F]
    val dis = new DisassemblerRunner[F]

    (cpl.runCompilerWithSbt(cmpOpt)(src)
      >>= dis.findClassFiles
      >>= dis.disassemble(disOpt))
      .as(ExitCode.Success)
  }
}