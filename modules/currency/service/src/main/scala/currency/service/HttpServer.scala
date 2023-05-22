package currency.service

import flywayutil.FlywayMigration

import zio.{Console, ExitCode, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.http.{Server, App}

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

import currency.service.rest.CurrencyEndpoints
import currency.service.rest.HelloEndpoint
import currency.service.rest.ProviderEndpoint
import scalapb.zio_grpc.ServiceList
import scalapb.zio_grpc.ServerLayer
import io.grpc.ServerBuilder
import co.ledger.cal.currencies.grpc.currency.ZioCurrency

import rest.RestApp
import grpc.GrpcApp
import currency.service.grpc.CurrencyService
//import javax.sql.DataSource

object Main extends ZIOAppDefault:
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = for
    config <- AppConfig.load
    _ <- ZIO.logInfo(config.database.host)
    _ <- FlywayMigration.migrate(config.database)
    _ <- app(config)
  yield ()

  private def servers(config: AppConfig) =
    GrpcApp.server.launch
      <&>
        RestApp.server

  private def app(config: AppConfig) =
    servers(config)
      .provide(
        DataSource.fromConfig(config.database.asConfig),
        QuillCurrencyRepository.live,
        QuillProviderRepository.live,
        CurrencyUseCase.live

//      ProviderUsecase.live,
//      ZLayer.Debug.mermaid
      )
