val scala3Version = "3.2.2"

inThisBuild(
  Seq(
    scalaVersion := scala3Version,
    scalafmtAll := true,
    scalafmtOnCompile := true
  )
)

lazy val `currency-core` = module("currency", "core")
  .settings(
    libraryDependencies := Dependencies.coreDependencies
  )
lazy val `currency-persistence` = module("currency", "persistence")
  .settings(
    libraryDependencies := Dependencies.quillDependencies
  )
  .dependsOn(`currency-core`)

lazy val `currency-service` = module("currency", "service")
  .dependsOn(`currency-core`, `currency-persistence`)
  .settings(
    libraryDependencies := Dependencies.appDependencies
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "zio-hexagonal-app"
  )
  .aggregate(`currency-service`, `currency-core`, `currency-persistence`)

def module(moduleId: String, projectId: String): Project =
  Project(
    id = s"$moduleId-$projectId",
    base = file(s"modules/$moduleId/$projectId")
  ).settings(
    name := s"$moduleId/$projectId",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )
