package com.htmlism

import scala.sys.process._

object Main {
  def main(args: Array[String]): Unit =
    args.lift(0) match {
      case Some(s) =>
        compile(s)

      case None =>
        throw new IllegalArgumentException("need to specify a file")
    }

  def compile(s: String) = {
    val allPhases = (1 to 24).mkString(",")

    println(Seq("scalac", "-Xprint:" + allPhases, "-d", "/tmp", s).!!)
  }
}
