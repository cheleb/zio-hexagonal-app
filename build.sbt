import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.2.2"

val dev = sys.env.get("DEV").isDefined

val serverPlugins = dev match {
  case true  => Seq()
  case false => Seq(SbtWeb, JavaAppPackaging, WebScalaJSBundlerPlugin)
}

val serverSettings = dev match {
  case true => Seq()
  case false =>
    Seq(
      Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
      Assets / WebKeys.packagePrefix := "public/",
      Runtime / managedClasspath += (Assets / packageBin).value
    )
}

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

lazy val `common-http` = module("common", "http")
  .settings(
    libraryDependencies ++= Dependencies.httpServer
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
  .dependsOn(`common-http`, `currency-core`, `currency-persistence`)
  .settings(
    libraryDependencies := Dependencies.appDependencies
  )
  .settings(dockerSettings)

lazy val `cal-client` = scalajsProject("cal", "client")
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(scalaJSModule)
//        .withSourceMap(true)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("client")))
    }
  )
  .settings(scalacOptions ++= usedScalacOptions)
  .settings(
    libraryDependencies += "dev.cheleb" %%% "laminar-form-derivation" % "0.0.1"
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/cal/shared"))
  .settings(
    publish / skip := true
  )
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val `cal-server` = module("cal", "server")
  .enablePlugins(serverPlugins: _*)
  .settings(
    cancelable := true,
    fork := true,
    scalaJSProjects := Seq(`cal-client`),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    libraryDependencies ++= Dependencies.httpServer
  )
  .settings(serverSettings: _*)
  .dependsOn(`common-http`, sharedJvm)
  .settings(
    publish / skip := true
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "zio-hexagonal-app"
  )
  .aggregate(
    `currency-module`,
    `cal-module`
  )

lazy val `cal-module` = project.aggregate(`cal-client`, `cal-server`)
lazy val `currency-module` =
  project.aggregate(`currency-core`, `currency-persistence`, `currency-service`)

def module(moduleId: String, projectId: String): Project =
  Project(
    id = s"$moduleId-$projectId",
    base = file(s"modules/$moduleId/$projectId")
  ).settings(
    name := s"$moduleId/$projectId",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )

def scalaJSModule = dev match {
  case true  => ModuleKind.ESModule
  case false => ModuleKind.CommonJSModule
}
val usedScalacOptions = Seq(
  "-encoding",
  "utf8",
  "-unchecked",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xmax-inlines:64"
)

def scalaJSPlugin = dev match {
  case true  => ScalaJSPlugin
  case false => ScalaJSBundlerPlugin
}

def scalajsProject(moduleId: String, projectId: String): Project =
  module(moduleId, projectId)
    .enablePlugins(scalaJSPlugin)
    .settings(nexusNpmSettings)
    .settings(Test / requireJsDomEnv := true)
    .settings(
      scalacOptions := Seq(
        "-scalajs",
        "-deprecation",
        "-feature",
        "-Xfatal-warnings"
      )
    )

def nexusNpmSettings =
  sys.env
    .get("NEXUS")
    .map(url =>
      npmExtraArgs ++= Seq(
        s"--registry=$url/repository/npm-public/"
      )
    )
    .toSeq
