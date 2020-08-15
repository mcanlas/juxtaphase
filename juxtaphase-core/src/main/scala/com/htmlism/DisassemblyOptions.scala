package com.htmlism

object DisassemblyOptions {
  val flags: Map[String, DisassemblyOptions => Boolean] =
    Map(
      "v" -> { _.verbose },
      "p" -> { _.showPrivateMembers },
      "c" -> {
        _.printByteCode
      }
    )

  val empty: DisassemblyOptions = DisassemblyOptions(verbose = false, showPrivateMembers = false, printByteCode = false)
}

final case class DisassemblyOptions(verbose: Boolean, showPrivateMembers: Boolean, printByteCode: Boolean) {
  def flags: Seq[String] =
    DisassemblyOptions.flags.foldLeft(Seq[String]()) { (acc, kv) =>
      if (kv._2(this))
        ("-" + kv._1) +: acc
      else
        acc
    }
}
