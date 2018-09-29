import postfix._

lazy val root = Project("juxtaphase", file("."))
  .settings(commonSettings: _*)
  .aggregate(console, web)

lazy val core = projectAt("core")
  .settings(commonSettings: _*)
  .settings(scalaFileIo: _*)
  .settings(catsIo: _*)

lazy val console = projectAt("console")
  .settings(commonSettings: _*)
  .dependsOn(core)

lazy val web = projectAt("web")
  .settings(commonSettings: _*)
  .enablePlay
  .dependsOn(core)

def projectAt(s: String) = Project("juxtaphase-" + s, file("juxtaphase-" + s))

lazy val commonSettings = Seq(scalafmtOnCompile := true,
                              organization := "com.htmlism",
                              scalaVersion := "2.12.7",
                              crossScalaVersions := Seq("2.11.12", "2.12.7"))

lazy val catsIo =
  Seq(
    libraryDependencies ++= Seq("org.typelevel" %% "mouse"       % "0.18",
                                "org.typelevel" %% "cats-effect" % "1.0.0"))

lazy val scalaFileIo =
  Seq(libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.6.0")
