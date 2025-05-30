lazy val root = Project("juxtaphase", file("."))
  .settings(commonSettings*)
  .aggregate(console)

lazy val core = projectAt("core")
  .settings(commonSettings*)
  .settings(scalaFileIo*)
  .settings(catsIo*)

lazy val console = projectAt("console")
  .settings(commonSettings*)
  .dependsOn(core)

def projectAt(s: String) = Project("juxtaphase-" + s, file("juxtaphase-" + s))

lazy val commonSettings = Seq(
  organization := "com.htmlism"
)

lazy val catsIo =
  Seq(libraryDependencies ++= Seq("org.typelevel" %% "cats-effect" % "3.6.1"))

lazy val scalaFileIo =
  Seq(libraryDependencies += "com.github.pathikrit" % "better-files_2.13" % "3.9.2")
