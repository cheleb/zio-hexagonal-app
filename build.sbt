val scala3Version = "3.2.2"

inThisBuild(
  Seq(
    version := "0.1.2-SNAPSHOT",
    scalaVersion := scala3Version,
    scalafmtAll := true,
    scalafmtOnCompile := true
  )
)

lazy val dockerSettings = Seq(
  dockerBaseImage := "azul/zulu-openjdk-centos:19.0.2-19.32.13",
  dockerUpdateLatest := true,
//  Docker / dockerRepository := Some("hub.docker.com"),
  Docker / dockerUsername := Some("cheleb"),
  dockerExposedPorts := Seq(8080),
  publish / skip := true
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
  .enablePlugins(
    BuildInfoPlugin,
    JavaServerAppPackaging,
//    JavaAgent,
    DockerPlugin
  )
  .dependsOn(`currency-core`, `currency-persistence`)
  .settings(
    libraryDependencies := Dependencies.appDependencies
  )
  .settings(dockerSettings)

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
