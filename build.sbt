import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.2.2"

val dev = sys.env.get("DEV").isDefined

val dockerPlugins = Seq(
  JavaServerAppPackaging,
//    JavaAgent,
  DockerPlugin
)

val serverPlugins = dev match {
  case true => Seq(BuildInfoPlugin, DockerPlugin)
  case false =>
    Seq(
//      SbtWeb,
      JavaServerAppPackaging,
      WebScalaJSBundlerPlugin,
      DockerPlugin
    )
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
    scalaVersion := scala3Version,
    run / fork := true,
    scalafmtAll := true,
    scalafmtOnCompile := true
  )
)

lazy val dockerSettings = Seq(
  dockerBaseImage := "azul/zulu-openjdk-centos:19.0.2-19.32.13-arm64",
  dockerUpdateLatest := true,
  Docker / dockerRepository := Some("localhost:5000"),
  Docker / dockerUsername := Some("cheleb"),
  dockerExposedPorts := Seq(8080)
)

lazy val `common-http` = module("common", "http")
  .settings(
    libraryDependencies ++= Dependencies.httpServer
  )
  .settings(
    publish / skip := true
  )

lazy val `currency-core` = module("currency", "core")
  .settings(
    libraryDependencies := Dependencies.coreDependencies
  )
  .settings(
    publish / skip := true
  )
lazy val `currency-persistence` = module("currency", "persistence")
  .settings(
    libraryDependencies := Dependencies.quillDependencies
  )
  .dependsOn(`currency-core`)
  .settings(
    publish / skip := true
  )

lazy val `currency-service` = module("currency", "service")
  .enablePlugins(dockerPlugins: _*)
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
    libraryDependencies ++= Seq(
      "dev.cheleb" %%% "laminar-form-derivation" % JSVersions.laminarFormDerivation
    )
  )
  .dependsOn(sharedJs)
  .settings(
    publish / skip := true
  )

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/cal/shared"))
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %%% "core" % JSVersions.sttpClient,
      "io.github.iltotore" %% "iron-zio-json" % JSVersions.iron,
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-core" % "2.21.2",
      "com.github.plokhotnyuk.jsoniter-scala" %%% "jsoniter-scala-macros" % "2.21.2",
      "com.softwaremill.sttp.client3" %%% "jsoniter" % JSVersions.sttpClient,
      "com.softwaremill.sttp.client3" %%% "zio-json" % JSVersions.sttpClient // for ZIO 2.x
    )
  )
  .settings(
    publish / skip := true
  )
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val `cal-server` = module("cal", "server")
  .enablePlugins(serverPlugins: _*)
  .settings(
    fork := true,
    scalaJSProjects := Seq(`cal-client`),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    libraryDependencies ++= Dependencies.httpServer,
    libraryDependencies += "com.softwaremill.sttp.client3" %% "zio" % "3.8.11" // for ZIO 2.x
  )
  .settings(serverSettings: _*)
  .dependsOn(`common-http`, sharedJvm)
  .settings(dockerSettings)

lazy val root = project
  .in(file("."))
  .settings(
    name := "zio-hexagonal-app"
  )
  .aggregate(
    `currency-module`,
    `cal-module`
  )
  .settings(publish / skip := true)

lazy val `cal-module` = project
  .in(file("modules/cal"))
  .aggregate(`cal-client`, `cal-server`)
  .settings(
    publish / skip := true,
    publishArtifact := false,
    Compile / skip := true
  )

lazy val `currency-module` =
  project
    .in(file("modules/currency"))
    .aggregate(`currency-core`, `currency-persistence`, `currency-service`)
    .settings(publish / skip := true, publishArtifact := false)

def module(moduleId: String, projectId: String): Project =
  Project(
    id = s"$moduleId-$projectId",
    base = file(s"modules/$moduleId/$projectId")
  ).enablePlugins(BuildInfoPlugin)
    .settings(buildInfoPackage := s"$moduleId.$projectId")
    .settings(
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
