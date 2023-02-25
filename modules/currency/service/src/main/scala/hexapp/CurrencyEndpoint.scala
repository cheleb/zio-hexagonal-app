package hexapp

import core.*

import sttp.capabilities.zio.ZioStreams
import sttp.tapir.*
import sttp.tapir.json.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio._
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.CodecFormat.TextPlain

import zio.*

import ziohttp.ZIOHttp
import _root_.zio.json.JsonCodec
import _root_.zio.json.JsonEncoder
import _root_.zio.json.internal.Write
import _root_.zio.json.JsonDecoder
import _root_.zio.json.JsonError
import _root_.zio.json.internal.RetractReader
import _root_.zio.json.DeriveJsonCodec
object CurrencyEndpoints:

  given Schema[CurrencyCode] = Schema.string[CurrencyCode]
  given Codec[String, CurrencyCode, TextPlain] =
    Codec.string.mapDecode(str => DecodeResult.Value(CurrencyCode(str)))(
      _.toString()
    )

  given JsonDecoder[CurrencyCode] =
    JsonDecoder[String].map(CurrencyCode.apply)
  given JsonEncoder[CurrencyCode] =
    JsonEncoder[String].contramap(_.toString())
  given JsonCodec[Currency] = DeriveJsonCodec.gen[Currency]

  val createCurrencyEndpoint: PublicEndpoint[Currency, Unit, Unit, Any] =
    endpoint.post
      .in("currency")
      .in(jsonBody[Currency])
      .out(emptyOutput)

  val listCurrencyEndpoint
      : PublicEndpoint[Option[CurrencyCode], Unit, List[Currency], Any] =
    endpoint.get
      .in("currency")
      .in(query[Option[CurrencyCode]]("code"))
      .out(jsonBody[List[Currency]])

  val createCurrencyServerEndpoint: ZServerEndpoint[CurrencyUseCase, Any] =
    createCurrencyEndpoint
      .serverLogicError(currency =>
        CurrencyUseCase
          .persist(currency)
      )

  val listCurrencyServerEndpoint: ZServerEndpoint[CurrencyUseCase, Any] =
    listCurrencyEndpoint
      .serverLogicSuccess {
        case Some(code) => CurrencyUseCase.find(code).map(_.toList)
        case None       => CurrencyUseCase.list
      }

  val apiDocEndpoints = List(
    createCurrencyEndpoint,
    listCurrencyEndpoint
  )

  val apiEndpoints =
    ZIOHttp.toHttp(
      createCurrencyServerEndpoint :: listCurrencyServerEndpoint :: Nil
    )
