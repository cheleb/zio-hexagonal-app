package currency.service

import flywayutil.FlywayMigration

import zio.{Console, ExitCode, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.http.{Server, ServerConfig, App}

import currency.core.CurrencyUseCase
import currency.core.ProviderUsecase
import currency.core.Provider
import currency.core.Currency

import persistance.QuillCurrencyRepository
import io.getquill.jdbczio.Quill.DataSource
import sttp.tapir.server.interceptor.metrics.MetricsRequestInterceptor
import persistance.QuillProviderRepository
import sttp.capabilities.zio.ZioStreams

import ziohttp.MetricsEndpoints

import sttp.tapir.Endpoint
import java.io.IOException

import zio.ZLayer

import ziohttp.ZIOHttp

import currency.service.CurrencyEndpoints
import currency.service.HelloEndpoint
import currency.service.ProviderEndpoint
import scalapb.zio_grpc.ServiceList
import scalapb.zio_grpc.ServerLayer
import io.grpc.ServerBuilder
import scalapb.zio_grpc.{Server => GrpcServer}
import co.ledger.cal.currencies.grpc.currency.ZioCurrency
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

    val port = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8000)

    // grpcApp.launch <&>
    Server
      .serve(app.withDefaultErrorResponse)
      .provide(
        ServerConfig.live(ServerConfig.default.port(port)),
        Server.live,
        DataSource.fromConfig(config.database.asConfig),
        QuillCurrencyRepository.live,
        QuillProviderRepository.live,
        CurrencyUseCase.live
//        ProviderUsecase.live,
//        ZLayer.Debug.tree
      )

  private def grpcApp(
      config: AppConfig
  ): ZLayer[CurrencyService, Throwable, GrpcServer] =
    val serviceList =
      ServiceList.addFromEnvironment[CurrencyService]
    val serverLayer =
      ServerLayer.fromServiceList(
        ServerBuilder.forPort(9000),
        serviceList
      )

    ZLayer.makeSome[CurrencyService, GrpcServer](
      serverLayer
    )
