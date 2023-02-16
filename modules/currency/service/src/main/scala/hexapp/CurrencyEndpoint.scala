package hexapp

import core.*

import com.github.plokhotnyuk.jsoniter_scala.core.JsonReader
import com.github.plokhotnyuk.jsoniter_scala.core.JsonWriter
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker

import sttp.capabilities.zio.ZioStreams
import sttp.tapir.*
import sttp.tapir.json.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.jsoniter.*
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.CodecFormat.TextPlain

import zio.*

import ziohttp.ZIOHttp
object CurrencyEndpoints:

  given JsonValueCodec[CurrencyCode] =
    new JsonValueCodec[CurrencyCode] {
      def decodeValue(in: JsonReader, default: CurrencyCode): CurrencyCode =
        CurrencyCode(in.readString(""))

      def encodeValue(x: CurrencyCode, out: JsonWriter): Unit =
        out.writeVal(x.toString())

      val nullValue: CurrencyCode = null.asInstanceOf[CurrencyCode]
    }

  given codecCurrency: JsonValueCodec[Currency] = JsonCodecMaker.make
  given codecCurrencie: JsonValueCodec[List[Currency]] = JsonCodecMaker.make
  given codecBooks: JsonValueCodec[Provider] = JsonCodecMaker.make

  given Schema[CurrencyCode] = Schema.string[CurrencyCode]
  given Codec[String, CurrencyCode, TextPlain] =
    Codec.string.mapDecode(str => DecodeResult.Value(CurrencyCode(str)))(
      _.toString()
    )

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
