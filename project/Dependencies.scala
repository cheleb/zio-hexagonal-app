import sbt._

object JSVersions {
  val laminarFormDerivation = "0.7.0"
  val sttpClient = "3.8.11"
  val iron = "2.0.0"
}

object Dependencies {

  private object Versions {
    val iron = "2.0.0"
    val flyway = "9.21.1"
    val postgresql = "42.6.0"
    val quill = "4.6.0.1"
    val munit = "0.7.29"
    val zioPravega = "0.6.1"
    val scala3 = "3.2.2"
    val tapir = "1.4.0"
    val zio = "2.0.13"
    val zioConfig = "3.0.7"
    val zioHttp = "3.0.0-RC1"

  }

  lazy val zioDependencies = Seq(
    "dev.zio" %% "zio" % Versions.zio,
    "dev.zio" %% "zio-config-typesafe" % Versions.zioConfig,
    "dev.zio" %% "zio-test" % Versions.zio % Test,
    "dev.zio" %% "zio-test-sbt" % Versions.zio % Test
  )

  lazy val rdbmsDependencies = zioDependencies ++ Seq(
    "org.flywaydb" % "flyway-core" % Versions.flyway,
    "org.postgresql" % "postgresql" % Versions.postgresql % Runtime
  )

  lazy val coreDependencies = zioDependencies ++ Seq(
    "dev.zio" %% "zio-streams" % Versions.zio,
    "io.github.iltotore" %% "iron-zio-json" % Versions.iron,
    "org.scalameta" %% "munit" % Versions.munit % Test
  )

  lazy val pravegaDependencies = zioDependencies ++ Seq(
    "dev.cheleb" %% "zio-pravega" % Versions.zioPravega
  )

  lazy val appDependencies =
    httpServerDependencies ++ Seq(
      "dev.zio" %% "zio-config" % Versions.zioConfig,
      "dev.zio" %% "zio-config-magnolia" % Versions.zioConfig,
      "org.postgresql" % "postgresql" % Versions.postgresql % Runtime
    )

  val quillDependencies = Seq(
    "io.getquill" %% "quill-jdbc-zio" % Versions.quill,
    "org.postgresql" % "postgresql" % "42.6.0" % "runtime",
    "ch.qos.logback" % "logback-classic" % "1.4.11" % "runtime"
  )

  lazy val httpServerDependencies = zioDependencies ++ Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-jsoniter-scala" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % Versions.tapir,
    "ch.qos.logback" % "logback-classic" % "1.4.11",
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % Versions.tapir % Test
  )
}
