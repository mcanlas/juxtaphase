package com.htmlism

object CommandLineFlags {
  type Builder[A] = A => A

  private val prefix = "JK_"

  private val keys =
    Map[String, Builder[DisassemblyOptions]](
      "PV" -> { _.copy(showPrivateMembers = true) },
      "C" -> { _.copy(printByteCode = true) },
      "V" -> { _.copy(verbose = true) })

  def detectDisassemblyOptions: DisassemblyOptions =
    keys.foldLeft(DisassemblyOptions.empty) { (acc, kv) =>
      if (keyExists(kv._1))
        kv._2(acc)
      else
        acc
    }

  private def keyExists(k: String): Boolean =
    Option(System.getenv(prefix + k)).isDefined
}
