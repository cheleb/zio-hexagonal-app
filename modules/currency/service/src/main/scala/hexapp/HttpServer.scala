package hexapp

import flywayutil.FlywayMigration

import zio.{Console, ExitCode, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.http.{Server, ServerConfig, HttpApp}

import core.CurrencyUseCase
import core.ProviderUsecase
import core.Provider
import core.Currency

import persistance.QuillCurrencyRepository
import io.getquill.jdbczio.Quill.DataSource
import sttp.tapir.server.interceptor.metrics.MetricsRequestInterceptor
import persistance.QuillProviderRepository
import sttp.capabilities.zio.ZioStreams
import metrics.MetricsEndpoints

import sttp.tapir.Endpoint
import java.io.IOException

import zio.ZLayer
import ziohttp.ZIOHttp

object Main extends ZIOAppDefault:
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = for
    config <- AppConfig.load
    _ <- ZIO.logInfo(config.database.host)
    _ <- FlywayMigration.migrate(config.database)
    _ <- app(config)
  yield ()

  private def app(config: AppConfig) =

    val currencyDocEndpoints = ZIOHttp.swagger(
      "hexagonal-app",
      "1.0.0",
      HelloEndpoint.apiDocEndpoints ++
        CurrencyEndpoints.apiDocEndpoints ++
        ProviderEndpoint.apiDocEndpoints
    )

    val app = HelloEndpoint.apiEndpoints ++
      CurrencyEndpoints.apiEndpoints ++
      ProviderEndpoint.apiEndpoints.provideLayer(ProviderUsecase.live) ++
      //
      ZIOHttp.toHttp(currencyDocEndpoints) ++
      ZIOHttp.toHttp(MetricsEndpoints.metricsEndpoint)

    val port = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8080)

    startServer(app)
      .provide(
        ServerConfig.live(ServerConfig.default.port(port)),
        Server.live,
        DataSource.fromConfig(config.database.asConfig),
        QuillCurrencyRepository.live,
        QuillProviderRepository.live,
        CurrencyUseCase.live
//        ProviderUsecase.live
        // ZLayer.Debug.tree
      )

  def startServer[R](
      app: HttpApp[R, Throwable]
  ): ZIO[R & Server, IOException, Int] =
    for
      actualPort <- Server.serve(app)
      _ <- Console.printLine(
        s"Go to http://localhost:${actualPort}/docs to open SwaggerUI. Press ENTER key to exit."
      )
      _ <- Console.readLine
    yield actualPort
