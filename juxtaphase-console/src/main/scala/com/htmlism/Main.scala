package com.htmlism

import cats.effect._
import cats.implicits._
import mouse.all._

/**
  * {{{
  *   sbt "juxtaphase-console/run juxtaphase-core/src/main/scala/com/htmlism/CompilerOptions.scala"
  * }}}
  */
object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    new Pipeline[IO]
      .process(args)
}

class Pipeline[F[_]](implicit F: Sync[F]) {
  def process(args: List[String]): F[ExitCode] =
    args |> EnvironmentReader.getSourceFile |> maybeRun

  private def maybeRun(src: Option[String]) =
    src.fold(zero)(run)

  private def zero =
    F.delay { Console.err.println("need to specify a file") }
      .as(ExitCode.Error)

  private def run(src: String) = {
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
