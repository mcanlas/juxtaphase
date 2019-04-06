package com.htmlism

sealed trait JxResult

final case class CompilerError(output: String) extends JxResult

final case class CompiledArtifacts(files: Seq[DisassembledClass])
