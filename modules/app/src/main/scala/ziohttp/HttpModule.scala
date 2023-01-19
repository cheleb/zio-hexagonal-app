package ziohttp

import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.*
import sttp.capabilities.zio.ZioStreams
import zio.http.Http
import zio.http.Request
import zio.http.Response

trait HttpModule[R] {
  def apiEndpoints: Http[R, Throwable, Request, Response]
//  def apiDocEndpoints: List[PublicEndpoint[I, Unit, String, Any]]
}
