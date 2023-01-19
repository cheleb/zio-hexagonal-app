val scala3Version = "3.2.2"

inThisBuild(
  Seq(
    scalaVersion := scala3Version,
//    run / fork := true,
    cancelable := true
  )
)

lazy val core = module("core")
  .settings(
    libraryDependencies := Dependencies.coreDependencies
  )
lazy val persistence = module("persistence")
  .settings(
    libraryDependencies := Dependencies.quillDependencies
  )
  .dependsOn(core)

lazy val app = module("app")
  .dependsOn(core, persistence)
  .settings(
    libraryDependencies := Dependencies.appDependencies
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "zio-hexagonal-app"
  )
  .aggregate(core, persistence)

def module(projectId: String, folder: Option[String] = None): Project =
  Project(
    id = projectId,
    base = file(s"modules/${folder.getOrElse(projectId)}")
  ).settings(
    name := projectId,
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )
