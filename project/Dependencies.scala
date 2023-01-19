import sbt._

object Dependencies {

  private object Versions {
    val quill = "4.6.0"
    val scala3 = "3.2.2"
    val tapir = "1.2.6"
    val zio = "2.0.5"
  }

  lazy val coreDependencies = Seq(
    "dev.zio" %% "zio" % Versions.zio,
    "dev.zio" %% "zio-streams" % Versions.zio,
    "dev.zio" %% "zio-test" % Versions.zio % Test
  )

  lazy val appDependencies =
    httpServer :+ "org.postgresql" % "postgresql" % "42.2.8" % "runtime"

  val quillDependencies = Seq(
    "io.getquill" %% "quill-jdbc-zio" % Versions.quill,
    "org.postgresql" % "postgresql" % "42.2.8" % "runtime",
    "ch.qos.logback" % "logback-classic" % "1.2.11" % "runtime"
  )

  private val httpServer = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Versions.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-jsoniter-scala" % Versions.tapir,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.20.3",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.20.3",
    "ch.qos.logback" % "logback-classic" % "1.4.5",
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % Versions.tapir % Test,
    "dev.zio" %% "zio-test" % "2.0.5" % Test,
    "dev.zio" %% "zio-test-sbt" % "2.0.5" % Test,
    "com.softwaremill.sttp.client3" %% "jsoniter" % "3.8.8" % Test
  )
}
