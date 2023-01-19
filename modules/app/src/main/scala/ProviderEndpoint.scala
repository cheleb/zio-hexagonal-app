import sttp.tapir.*
import sttp.tapir.json.*
import sttp.tapir.ztapir.ZServerEndpoint
import zio.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.generic.auto._
import sttp.tapir.json.jsoniter.*
import core.Provider
import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import core.ProviderUsecase
import core.ProviderRepository
import persistance.QuillCurrencyRepository
import io.getquill.jdbczio.Quill.DataSource
import javax.sql.DataSource
import core.Provider

object ProviderEndpoint {

  given codecBooks: JsonValueCodec[Provider] = JsonCodecMaker.make

  val createProviderEndpoint: PublicEndpoint[Provider, Unit, Unit, Any] =
    endpoint.post
      .in("provider")
      .in(jsonBody[Provider])
      .out(emptyOutput)

  val createProviderServerEndpoint: ZServerEndpoint[ProviderUsecase, Any] =
    createProviderEndpoint
      .serverLogicError(provider =>
        ProviderUsecase
          .persist(provider)
      )

  val apiDocEndpoints =
    List(createProviderEndpoint)

  val apiEndpoints =
    List(createProviderServerEndpoint)

  val all: List[ZServerEndpoint[ProviderUsecase, Any]] =
    apiEndpoints

}
