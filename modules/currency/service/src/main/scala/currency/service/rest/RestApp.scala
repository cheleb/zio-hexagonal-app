package currency.service.rest

import ziohttp.ZIOHttp
import currency.core.ProviderUsecase
import ziohttp.MetricsEndpoints
import zio.http.Server
import zio.http.ServerConfig
import zio.ZIO
import currency.core.CurrencyUseCase
import currency.core.ProviderRepository

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
        ServerConfig.live(ServerConfig.default.port(port)),
        Server.live
      )

}
