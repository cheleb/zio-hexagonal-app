package currency.service.rest

import sttp.tapir.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio.*

import sttp.model.StatusCode
import zio.*
import ziohttp.*
import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

import currency.core.usecases.ProviderUsecase
import currency.core.Provider
import currency.core.usecases.ProviderUsecase
import currency.core.Provider
object ProviderEndpoint:

  given JsonCodec[Provider] = DeriveJsonCodec.gen

  val createProviderEndpoint: PublicEndpoint[Provider, Unit, Unit, Any] =
    endpoint.post
      .in("provider")
      .in(jsonBody[Provider])
      .out(statusCode(StatusCode.NoContent))

  val createProviderServerEndpoint: ZServerEndpoint[ProviderUsecase, Any] =
    createProviderEndpoint
      .serverLogicSuccess(provider =>
        ProviderUsecase
          .persist(provider)
      )

  val apiDocEndpoints =
    List(createProviderEndpoint)

  val apiEndpoints =
    ZIOHttp
      .toHttp(List(createProviderServerEndpoint))
