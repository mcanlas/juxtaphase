package com.htmlism

import cats.effect._

class EnvironmentReader[F[_]](implicit F: Sync[F]) {
  type Builder[A] = A => A
  type StrBuilder[A] = (A, String) => A

  private val prefix = "JK_"

  private val disassemblyKeys =
    Map[String, Builder[DisassemblyOptions]](
      "PV" -> { _.copy(showPrivateMembers = true) },
      "C" -> { _.copy(printByteCode = true) },
      "V" -> { _.copy(verbose = true) })

  private val compilerKeys =
    Map[String, StrBuilder[CompilerOptions]](
      "VER" -> { (o, s) => o.copy(scalaVersion = s) },
      "PHASES" -> { (o, s) => o.copy(phases = toIntSet(s)) }
    )

  private def toIntSet(s: String)  =
    s
      .split(",")
      .toSet[String] // toInt needs type hint
      .map(_.toInt)

  def detectDisassemblyOptions: F[DisassemblyOptions] =
    detectOptions(DisassemblyOptions.empty)(disassemblyKeys)

  private def detectOptions[A](empty: A)(keys: Map[String, Builder[A]]): F[A] =
    F.delay {
      keys.foldLeft(empty) { (acc, kv) =>
        if (keyExists(kv._1))
          kv._2(acc)
        else
          acc
      }
    }

  def detectCompilerOptions: F[CompilerOptions] =
    F.delay {
      compilerKeys.foldLeft(CompilerOptions.empty) { (acc, kv) =>
        valueAt(kv._1) match {
          case Some(v) => kv._2(acc, v)
          case None => acc
        }
      }
    }

  def getSourceFile(args: List[String]): F[String] =
    args
      .headOption
      .fold {
        F.raiseError[String](new IllegalArgumentException("need to specify a file"))
      }(F.pure)

  private def keyExists(k: String): Boolean =
    Option(System.getenv(prefix + k)).isDefined

  private def valueAt(k: String): Option[String] =
    Option(System.getenv(prefix + k))
}
