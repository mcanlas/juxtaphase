package com.htmlism

object CompilerOptions {
  private val defaultScalaVersion =
    "2.13.8"

  val empty: CompilerOptions =
    CompilerOptions(defaultScalaVersion, Set.empty)
}

final case class CompilerOptions(
    scalaVersion: String,
    phases: Set[Int]
)
