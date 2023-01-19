package ziohttp

import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.*
import sttp.capabilities.zio.ZioStreams

trait HttpModule[I, R] {
  def apiEndpoints: List[ZServerEndpoint[R, ZioStreams]]
  def apiDocEndpoints: List[PublicEndpoint[I, Unit, String, Any]]
}
