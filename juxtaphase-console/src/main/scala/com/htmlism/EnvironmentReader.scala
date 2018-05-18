package com.htmlism

import cats.effect._

class EnvironmentReader[F[_]](implicit F: Sync[F]) {
  type Builder[A] = A => A

  private val prefix = "JK_"

  private val disassemblyKeys =
    Map[String, Builder[DisassemblyOptions]](
      "PV" -> { _.copy(showPrivateMembers = true) },
      "C" -> { _.copy(printByteCode = true) },
      "V" -> { _.copy(verbose = true) })

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

  def getSourceFile(args: Array[String]): F[String] =
    F.delay {
      args
        .lift(0)
        .getOrElse(throw new IllegalArgumentException("need to specify a file"))
    }

  private def keyExists(k: String): Boolean =
    Option(System.getenv(prefix + k)).isDefined
}
