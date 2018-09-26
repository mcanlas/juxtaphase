import sbt._
import sbt.Keys._

import play.sbt._
import play.sbt.PlayImport._

object postfix {
  implicit class PlayOps(p: Project) {
    def enablePlay: Project =
      p.enablePlugins(PlayScala)
        .settings(libraryDependencies += guice)
  }
}
