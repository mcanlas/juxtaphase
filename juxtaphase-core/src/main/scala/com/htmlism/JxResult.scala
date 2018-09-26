package com.htmlism

sealed trait JxResult

case class CompilerError(output: String) extends JxResult

case class CompiledArtifacts(files: Seq[DisassembledClass])
