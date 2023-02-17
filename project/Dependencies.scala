import sbt._

object JSVersions {
  val laminarFormDerivation = "0.0.2"
  val sttpClient = "3.8.11"
}

object Dependencies {

  private object Versions {
    val flyway = "9.11.0"
    val postgresql = "42.2.8"
    val quill = "4.6.0.1"
    val scala3 = "3.2.2"
    val tapir = "1.2.8"
    val zio = "2.0.9"
    val zioConfig = "3.0.7"
    val zioHttp = "0.0.4"

  }

  lazy val coreDependencies = Seq(
    "dev.zio" %% "zio" % Versions.zio,
    "dev.zio" %% "zio-streams" % Versions.zio,
    "dev.zio" %% "zio-test" % Versions.zio % Test
  )

  lazy val appDependencies =
    httpServer ++ Seq(
      "dev.zio" %% "zio-config" % Versions.zioConfig,
      "dev.zio" %% "zio-config-magnolia" % Versions.zioConfig,
//      "dev.zio" %% "zio-config-refined" % Versions.zioConfig,
      "dev.zio" %% "zio-config-typesafe" % Versions.zioConfig,
      "org.flywaydb" % "flyway-core" % Versions.flyway,
      "org.postgresql" % "postgresql" % Versions.postgresql % "runtime"
    )

  val quillDependencies = Seq(
    "io.getquill" %% "quill-jdbc-zio" % Versions.quill,
    "org.postgresql" % "postgresql" % "42.2.8" % "runtime",
    "ch.qos.logback" % "logback-classic" % "1.2.11" % "runtime"
  )

  lazy val httpServer = Seq(
//    "dev.zio" %% "zio-http" % Versions.zioHttp,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-jsoniter-scala" % Versions.tapir,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.20.7",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.20.7",
    "ch.qos.logback" % "logback-classic" % "1.4.5",
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % Versions.tapir % Test,
    "dev.zio" %% "zio-test" % "2.0.9" % Test,
    "dev.zio" %% "zio-test-sbt" % "2.0.9" % Test,
    "com.softwaremill.sttp.client3" %% "jsoniter" % "3.8.11" % Test
  )
}
