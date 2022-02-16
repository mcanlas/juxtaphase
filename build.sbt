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
  organization := "com.htmlism",
  scalaVersion := "2.13.8",
  crossScalaVersions := Seq("2.13.8")
)

lazy val catsIo =
  Seq(libraryDependencies ++= Seq("org.typelevel" %% "cats-effect" % "3.2.9"))

lazy val scalaFileIo =
  Seq(libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1")
