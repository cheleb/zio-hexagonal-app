package currency.service.rest

import ziohttp.ZIOHttp
import currency.core.usecases.ProviderUsecase
import ziohttp.MetricsEndpoints
import zio.http.Server

import zio.ZIO
import currency.core.usecases.CurrencyUseCase
import currency.core.ports.ProviderRepository
import zio.ZLayer

object RestApp {

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

  def server: ZIO[CurrencyUseCase & ProviderRepository, Throwable, Nothing] =
    Server
      .serve(app.withDefaultErrorResponse)
      .provideSome[CurrencyUseCase & ProviderRepository](
        ZLayer.succeed(Server.Config.default.port(port)),
        Server.live
      )

}
