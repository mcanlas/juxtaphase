lazy val root = Project("juxtaphase", file("."))
  .aggregate(console, web)

lazy val core = projectAt("core")

lazy val console = projectAt("console")
  .dependsOn(core)

lazy val web = projectAt("web")
  .dependsOn(core)

def projectAt(s: String) = Project("juxtaphase-" + s, file("juxtaphase-" + s))
