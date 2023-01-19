import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zio.{Console, ExitCode, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.http.{Server, ServerConfig, HttpApp}
import sttp.tapir.ztapir.ZServerEndpoint

import core.CreateCurrency
import persistance.QuillCurrencyRepository
import io.getquill.jdbczio.Quill.DataSource
import sttp.tapir.server.interceptor.metrics.MetricsRequestInterceptor
import core.ProviderUsecase
import persistance.QuillProviderRepository
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.swagger.SwaggerUIOptions
import sttp.capabilities.zio.ZioStreams
import metrics.MetricsEndpoints
import zio.ZLayer
import ziohttp.ZIOHttp

import sttp.tapir.Endpoint
import core.Provider
import core.Currency

object Main extends ZIOAppDefault:

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =

    val currencyDocEndpoints = ZIOHttp.swagger(
      "hexagonal-app",
      "1.0.0",
      HelloEndpoint.apiDocEndpoints ++ CurrencyEndpoints.apiDocEndpoints ++ ProviderEndpoint.apiDocEndpoints
    )

    val app =
      List(
        HelloEndpoint.apiEndpoints,
        CurrencyEndpoints.apiEndpoints,
        ProviderEndpoint.apiEndpoints.provideLayer(ProviderUsecase.live),
        //
        ZioHttpInterpreter().toHttp(currencyDocEndpoints),
        ZIOHttp.toHttp(MetricsEndpoints.metricsEndpoint)
      ).reduce(_ ++ _)

    val port = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8080)

    (
      for
        actualPort <- Server.install(app)
        _ <- Console.printLine(
          s"Go to http://localhost:${actualPort}/docs to open SwaggerUI. Press ENTER key to exit."
        )
        _ <- Console.readLine
      yield ()
    ).provide(
      ServerConfig.live(ServerConfig.default.port(port)),
      Server.live,
      DataSource.fromPrefixClosable("db"),
      QuillCurrencyRepository.live,
      QuillProviderRepository.live,
      CreateCurrency.live
//      ZLayer.Debug.tree
//      ProviderUsecase.live
    ).exitCode
