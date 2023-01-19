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

case class User(name: String) extends AnyVal
object HelloEndpoint {
  val helloEndpoint: PublicEndpoint[User, Unit, String, Any] = endpoint.get
    .in("hello")
    .in(query[User]("name"))
    .out(stringBody)
  val helloServerEndpoint: ZServerEndpoint[Any, Any] =
    helloEndpoint.serverLogicSuccess(user =>
      ZIO.log("coucou")
        *> ZIO.succeed(s"Hello ${user.name}")
    )

  val apiDocEndpoints = List(helloEndpoint)

  val apiEndpoints =
    List(helloServerEndpoint)

  val all: List[ZServerEndpoint[Any, ZioStreams]] =
    apiEndpoints

}
