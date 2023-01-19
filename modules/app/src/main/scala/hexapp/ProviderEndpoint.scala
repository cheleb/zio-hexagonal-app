package hexapp

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import core.*
import sttp.tapir.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.jsoniter.*

import sttp.model.StatusCode
import zio.*
import ziohttp.*

object ProviderEndpoint:

  given JsonValueCodec[Provider] = JsonCodecMaker.make

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
