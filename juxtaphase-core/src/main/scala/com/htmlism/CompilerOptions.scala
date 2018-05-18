package com.htmlism

object CompilerOptions {
  private val defaultScalaVersion =
    "2.12.6"

  val empty: CompilerOptions =
    CompilerOptions(defaultScalaVersion, Set.empty)
}

case class CompilerOptions(
  scalaVersion: String,
  phases: Set[Int]
)
