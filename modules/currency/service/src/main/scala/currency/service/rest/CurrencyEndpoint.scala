package currency.service.rest

import sttp.capabilities.zio.ZioStreams
import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.CodecFormat.TextPlain

import zio.*

import ziohttp.ZIOHttp
import zio.json.JsonCodec
import zio.json.JsonEncoder
import zio.json.internal.Write
import zio.json.JsonDecoder
import zio.json.JsonError
import zio.json.internal.RetractReader
import zio.json.DeriveJsonCodec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.zioJson.given
import sttp.tapir.Schema.schemaForMap
import sttp.model.StatusCode

import currency.core.Currency
import currency.core.CurrencyPair
import currency.core.CurrencyUseCase
import currency.core.Currency.CurrencyCode
object CurrencyEndpoints:

  given Schema[CurrencyCode] = Schema.string[CurrencyCode]

  given Schema[IronType[Double, Positive]] =
    Schema.schemaForDouble.as[IronType[Double, Positive]]

  given Codec[String, CurrencyCode, TextPlain] =
    Codec.string.mapDecode(str => DecodeResult.Value(CurrencyCode(str)))(
      _.toString()
    )

  given JsonDecoder[CurrencyCode] =
    JsonDecoder[String].map(CurrencyCode.apply)
  given JsonEncoder[CurrencyCode] =
    JsonEncoder[String].contramap(_.toString())
  given JsonCodec[Currency] = DeriveJsonCodec.gen[Currency]

  given JsonCodec[CurrencyPair] = DeriveJsonCodec.gen[CurrencyPair]

  val createCurrencyEndpoint: PublicEndpoint[Currency, Unit, Unit, Any] =
    endpoint.post
      .in("currency")
      .in(jsonBody[Currency])
      .out(statusCode(StatusCode.NoContent))

  val listCurrencyEndpoint
      : PublicEndpoint[Option[CurrencyCode], Unit, List[Currency], Any] =
    endpoint.get
      .in("currency")
      .in(query[Option[CurrencyCode]]("code"))
      .out(jsonBody[List[Currency]])

  val createCurrencyPairEndpoint
      : PublicEndpoint[CurrencyPair, Unit, Unit, Any] =
    endpoint.post
      .in("pair")
      .in(jsonBody[CurrencyPair])
      .out(statusCode(StatusCode.NoContent))

  val listCurrencyPairEndpoint
      : PublicEndpoint[Option[CurrencyCode], Unit, List[CurrencyPair], Any] =
    endpoint.get
      .in("pair")
      .in(query[Option[CurrencyCode]]("code"))
      .out(jsonBody[List[CurrencyPair]])

  val createCurrencyServerEndpoint: ZServerEndpoint[CurrencyUseCase, Any] =
    createCurrencyEndpoint
      .serverLogicSuccess(currency =>
        CurrencyUseCase
          .persist(currency)
      )

  val listCurrencyServerEndpoint: ZServerEndpoint[CurrencyUseCase, Any] =
    listCurrencyEndpoint
      .serverLogicSuccess {
        case Some(code) => CurrencyUseCase.find(code).map(_.toList)
        case None       => CurrencyUseCase.list
      }

  val createCurrencyPairServerEndpoint: ZServerEndpoint[CurrencyUseCase, Any] =
    createCurrencyPairEndpoint
      .serverLogicSuccess { pair =>
//        CurrencyUseCase
//          .persist(pair)
        ZIO.debug(s"Persisting $pair")
      }

  val listCurrencyPairServerEndpoint: ZServerEndpoint[CurrencyUseCase, Any] =
    listCurrencyPairEndpoint
      .serverLogicSuccess {
        case Some(code) =>
          CurrencyUseCase.find(code).map(_.toList).map { currencies =>
            currencies.map { currency =>
              CurrencyPair(code, currency.code, 1)
            }
          }
        case None =>
          CurrencyUseCase.list.map { currencies =>
            currencies.map { currency =>
              CurrencyPair(currency.code, currency.code, 1)
            }
          }
      }

  val apiDocEndpoints = List(
    createCurrencyEndpoint,
    listCurrencyEndpoint,
    createCurrencyPairEndpoint,
    listCurrencyPairEndpoint
  )

  val apiEndpoints =
    ZIOHttp.toHttp(
      createCurrencyServerEndpoint :: listCurrencyServerEndpoint :: createCurrencyPairServerEndpoint :: listCurrencyPairServerEndpoint :: Nil
    )
