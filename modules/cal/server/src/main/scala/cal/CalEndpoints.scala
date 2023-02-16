package cal

import sttp.tapir.ztapir.*
import zio.*
import zio.http.Http
import sttp.tapir.static.StaticOutput
import sttp.tapir.json.jsoniter.*
import sttp.tapir.generic.auto.*

import sttp.tapir.static.Resources
import sttp.client3.*
import sttp.client3.jsoniter.*
import sttp.tapir.Schema
import sttp.tapir.Codec
import sttp.tapir.DecodeResult
import sttp.tapir.CodecFormat.TextPlain
import sttp.client3.httpclient.zio.HttpClientZioBackend

object CalEndpoints {

  private val backend = HttpURLConnectionBackend()

  given Schema[CurrencyCode] = Schema.string[CurrencyCode]
  given Codec[String, CurrencyCode, TextPlain] =
    Codec.string.mapDecode(str => DecodeResult.Value(CurrencyCode(str)))(
      _.toString()
    )

  val public: ZServerEndpoint[Any, Any] =
    resourcesGetServerEndpoint("public")(
      getClass.getClassLoader,
      "public"
    )

  val getCurrency: ZServerEndpoint[Any, Any] = endpoint.get
    .in("currency")
    .out(jsonBody[List[Currency]])
    .serverLogic(_ =>
      HttpClientZioBackend().flatMap { backend =>
        basicRequest
          .get(uri"http://currencies-svc:8000/currency")
          .response(asJson[List[Currency]])
          .send(backend)
          .map(_.body.left.map(_ => ()))
      }
    )

  val postCurrency: ZServerEndpoint[Any, Any] = endpoint.post
    .in("currency")
    .in(jsonBody[Currency])
    .out(jsonBody[Currency])
    .serverLogic((currency: Currency) =>
      ZIO.debug(currency) *>
        HttpClientZioBackend().flatMap { backend =>
          basicRequest
            .post(uri"http://currencies-svc:8000/currency")
            .body(currency)
            .send(backend)
        } *>
        ZIO.succeed(scala.util.Right(currency))
    )

  val all = List(public, postCurrency)

}
