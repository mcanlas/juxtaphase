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
  .settings(
    scalacOptions ++= Seq(
      s"-P:silencer:sourceRoots=${(Compile / routes / target).value}",
      s"-P:silencer:pathFilters=.*"
    ),
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
  )

def projectAt(s: String) = Project("juxtaphase-" + s, file("juxtaphase-" + s))

lazy val silencerVersion =
  "1.7.1"

lazy val commonSettings = Seq(
  scalafmtOnCompile := true,
  organization := "com.htmlism",
  scalaVersion := "2.13.4",
  crossScalaVersions := Seq("2.13.4")
)

lazy val catsIo =
  Seq(libraryDependencies ++= Seq("org.typelevel" %% "cats-effect" % "3.0.0-M5"))

lazy val scalaFileIo =
  Seq(libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1")
