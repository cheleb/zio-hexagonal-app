package hexapp

import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.ztapir.ZServerEndpoint
import zio.*
import ziohttp.*

case class User(name: String) extends AnyVal
object HelloEndpoint:

  val helloEndpoint: PublicEndpoint[User, Unit, Unit, Any] = endpoint.get
    .in("hello")
    .in(query[User]("name"))
    .out(statusCode(StatusCode.NoContent))

  val helloServerEndpoint: ZServerEndpoint[Any, Any] =
    helloEndpoint.serverLogicSuccess(user => ZIO.log("coucou"))

  val apiDocEndpoints = List(helloEndpoint)

  val apiEndpoints =
    ZIOHttp.toHttp(List(helloServerEndpoint))
