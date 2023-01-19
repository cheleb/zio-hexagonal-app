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

object CurrencyEndpoints {

  given codecBooks: JsonValueCodec[Currency] = JsonCodecMaker.make

  val createCurrencyEndpoint: PublicEndpoint[Currency, Unit, Unit, Any] =
    endpoint.post
      .in("currency")
      .in(jsonBody[Currency])
      .out(emptyOutput)

  val createCurrencyServerEndpoint: ZServerEndpoint[CreateCurrency, Any] =
    createCurrencyEndpoint
      .serverLogicError(currency =>
        CreateCurrency
          .persist(currency)
      )

  val apiDocEndpoints: List[PublicEndpoint[Currency, Unit, Unit, Any]] = List(
    createCurrencyEndpoint
  )

  val apiEndpoints =
    List(createCurrencyServerEndpoint)

  val all: List[ZServerEndpoint[CreateCurrency, ZioStreams]] =
    apiEndpoints

}
