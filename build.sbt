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
  .settings(scalacOptions -= "-Ywarn-unused-import") // silence unused import warnings from play routes
  .settings(scalacOptions -= "-Ywarn-unused:imports") // silence unused import warnings from play routes
  .settings(scalacOptions -= "-Ywarn-unused:locals")
  .settings(scalacOptions -= "-Wunused:locals")
  .settings(scalacOptions -= "-Ywarn-unused:privates")
  .settings(scalacOptions -= "-Wunused:privates")

def projectAt(s: String) = Project("juxtaphase-" + s, file("juxtaphase-" + s))

lazy val commonSettings = Seq(scalafmtOnCompile := true,
                              organization := "com.htmlism",
                              scalaVersion := "2.12.10",
                              crossScalaVersions := Seq("2.11.12", "2.12.10"))

lazy val catsIo =
  Seq(
    libraryDependencies ++= Seq("org.typelevel" %% "mouse"       % "0.24",
                                "org.typelevel" %% "cats-effect" % "2.1.0"))

lazy val scalaFileIo =
  Seq(libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0")
