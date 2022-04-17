lazy val root = Project("juxtaphase", file("."))
  .settings(commonSettings: _*)
  .aggregate(console)

lazy val core = projectAt("core")
  .settings(commonSettings: _*)
  .settings(scalaFileIo: _*)
  .settings(catsIo: _*)

lazy val console = projectAt("console")
  .settings(commonSettings: _*)
  .dependsOn(core)

def projectAt(s: String) = Project("juxtaphase-" + s, file("juxtaphase-" + s))

lazy val commonSettings = Seq(
  scalafmtOnCompile := true,
  organization := "com.htmlism"
)

lazy val catsIo =
  Seq(libraryDependencies ++= Seq("org.typelevel" %% "cats-effect" % "3.3.11"))

lazy val scalaFileIo =
  Seq(libraryDependencies += "com.github.pathikrit" % "better-files_2.13" % "3.9.1")
