package cal

import sttp.tapir.ztapir.*
import zio.*
import zio.http.Http
import sttp.tapir.static.StaticOutput

import sttp.tapir.static.Resources

object CalEndpoints {

  val public: ZServerEndpoint[Any, Any] =
    resourcesGetServerEndpoint("public")(
      getClass.getClassLoader,
      "public"
    )

  val all = List(public)

}
