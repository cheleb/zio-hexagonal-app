import sttp.tapir.*
import sttp.tapir.json.*
import sttp.tapir.ztapir.ZServerEndpoint
import zio.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.jsoniter.*
import core.Currency
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import core.CreateCurrency
import core.CurrencyRepository
import persistance.QuillCurrencyRepository
import io.getquill.jdbczio.Quill.DataSource
import javax.sql.DataSource

import ziohttp.HttpModule
import sttp.capabilities.zio.ZioStreams
import ziohttp.*

object CurrencyEndpoints extends HttpModule[CreateCurrency] {

  given codecCurrency: JsonValueCodec[Currency] = JsonCodecMaker.make
  given codecCurrencie: JsonValueCodec[List[Currency]] = JsonCodecMaker.make

  val createCurrencyEndpoint: PublicEndpoint[Currency, Unit, Unit, Any] =
    endpoint.post
      .in("currency")
      .in(jsonBody[Currency])
      .out(emptyOutput)

  val listCurrencyEndpoint: PublicEndpoint[Unit, Unit, List[Currency], Any] =
    endpoint.get
      .in("currency")
      .out(jsonBody[List[Currency]])

  val createCurrencyServerEndpoint: ZServerEndpoint[CreateCurrency, Any] =
    createCurrencyEndpoint
      .serverLogicError(currency =>
        CreateCurrency
          .persist(currency)
      )

  val listCurrencyServerEndpoint: ZServerEndpoint[CreateCurrency, Any] =
    listCurrencyEndpoint
      .serverLogicSuccess(_ => CreateCurrency.list)

  val apiDocEndpoints = List(
    createCurrencyEndpoint,
    listCurrencyEndpoint
  )

  val apiEndpoints =
    ZIOHttp.toHttp(
      List(createCurrencyServerEndpoint, listCurrencyServerEndpoint)
    )

}
