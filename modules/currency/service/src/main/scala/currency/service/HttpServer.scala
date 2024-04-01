package currency.service

import flywayutil.FlywayMigration

import zio.{Console, ExitCode, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.http.{Server, App}

import currency.core.usecases.CurrencyUseCase
import currency.core.usecases.ProviderUsecase
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

import currency.service.rest.CurrencyEndpoints
import currency.service.rest.HelloEndpoint
import currency.service.rest.ProviderEndpoint
// import scalapb.zio_grpc.ServiceList
// import scalapb.zio_grpc.ServerLayer
//import io.grpc.ServerBuilder
//import co.ledger.cal.currencies.grpc.currency.ZioCurrency

import rest.RestApp
// import grpc.GrpcApp
// import currency.service.grpc.CurrencyService
import currency.events.PravegaCurrencyStream
import zio.pravega.PravegaStream
import zio.pravega.PravegaClientConfig
import java.net.URI
//import javax.sql.DataSource

object Main extends ZIOAppDefault:
  override def run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] = for
    config <- AppConfig.load
    _ <- ZIO.logInfo(config.database.host)
    _ <- FlywayMigration.migrate(config.database)
    _ <- app(config)
  yield ()

  private def servers(config: AppConfig) =
    // GrpcApp.server.launch
    //   <&>
    RestApp.server

  def controllerURI(controllerIP: String): URI =
    URI.create(s"tcp://$controllerIP:9090")

  private def app(config: AppConfig) =
    servers(config)
      .provide(
        Scope.default,
        DataSource.fromConfig(config.database.asConfig),
        QuillCurrencyRepository.live,
        QuillProviderRepository.live,
        CurrencyUseCase.live,
        ZLayer.succeed(
          PravegaClientConfig.builder
            .controllerURI(controllerURI(config.pravega.controllerIP))
            .build()
        ),
        PravegaCurrencyStream.live,
        PravegaStream.fromScope("cal")

//      ProviderUsecase.live,
//      ZLayer.Debug.mermaid
      )
