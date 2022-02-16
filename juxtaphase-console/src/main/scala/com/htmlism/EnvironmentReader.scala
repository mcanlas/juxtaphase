package com.htmlism

class EnvironmentReader:
  import EnvironmentReader._

  type Builder[A] = A => A
  type StrBuilder[A] = (A, String) => A

  private val disassemblyKeys =
    Map[String, Builder[DisassemblyOptions]](
      "PV" -> {
        _.copy(showPrivateMembers = true)
      },
      "C" -> { _.copy(printByteCode = true) },
      "V" -> {
        _.copy(verbose = true)
      }
    )

  private val compilerKeys =
    Map[String, StrBuilder[CompilerOptions]](
      "VER" -> { (o, s) =>
        o.copy(scalaVersion = s)
      },
      "PHASES" -> { (o, s) =>
        o.copy(phases = toIntSet(s))
      }
    )

  private def toIntSet(s: String) =
    s.split(",")
      .toSet[String] // toInt needs type hint
      .map(_.toInt)

  def detectDisassemblyOptions: DisassemblyOptions =
    detectOptions(DisassemblyOptions.empty)(disassemblyKeys)

  private def detectOptions[A](empty: A)(keys: Map[String, Builder[A]]): A =
    keys.foldLeft(empty) { (acc, kv) =>
      if (keyExists(kv._1))
        kv._2(acc)
      else
        acc
    }

  def detectCompilerOptions: CompilerOptions =
    compilerKeys.foldLeft(CompilerOptions.empty) { (acc, kv) =>
      valueAt(kv._1) match
        case Some(v) => kv._2(acc, v)
        case None => acc
    }

object EnvironmentReader:
  private val prefix = "JK_"

  def keyExists(k: String): Boolean =
    Option(System.getenv(prefix + k)).isDefined

  def valueAt(k: String): Option[String] =
    Option(System.getenv(prefix + k))

  def getSourceFile(args: List[String]): Option[String] =
    args.headOption
