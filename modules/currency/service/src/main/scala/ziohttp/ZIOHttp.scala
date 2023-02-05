package ziohttp

import sttp.tapir.server.interceptor.metrics.MetricsRequestInterceptor
import zio.*
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.ztapir.ZServerEndpoint

import org.slf4j.LoggerFactory
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.capabilities.zio.ZioStreams
import _root_.metrics.MetricsEndpoints
import sttp.tapir.Endpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.interceptor.RequestInterceptor
import sttp.tapir.server.interceptor.RequestInterceptor.RequestResultEffectTransform
import sttp.tapir.server.interceptor.RequestResult
import sttp.tapir.model.ServerRequest

object ZIOHttp {
  val log = LoggerFactory.getLogger(ZioHttpInterpreter.getClass.getName)

  private val CorrelationIdHeader = "X-Correlation-ID"

  private val correlationIdInterceptors =
    RequestInterceptor.transformResultEffect(
      new RequestResultEffectTransform[Task] {

        override def apply[B](
            request: ServerRequest,
            result: Task[RequestResult[B]]
        ): Task[RequestResult[B]] =
          request.header(CorrelationIdHeader) match {
            case Some(cid) =>
              ZIO.logAnnotate("cid", cid)(result)
            case None =>
              result
          }
      }
    )

  private def serverOptions(
      metricsInterceptor: MetricsRequestInterceptor[Task]
  ): ZioHttpServerOptions[Any] =
    ZioHttpServerOptions.customiseInterceptors
      .serverLog(
        DefaultServerLog[Task](
          doLogWhenReceived = msg => ZIO.succeed(log.debug(msg)),
          doLogWhenHandled = (msg, error) =>
            ZIO.succeed(
              error.fold(log.debug(msg))(err => log.debug(msg, err))
            ),
          doLogAllDecodeFailures = (msg, error) =>
            ZIO.succeed(
              error.fold(log.debug(msg))(err => log.debug(msg, err))
            ),
          doLogExceptions =
            (msg: String, ex: Throwable) => ZIO.succeed(log.debug(msg, ex)),
          noLog = ZIO.unit
        )
      )
      .metricsInterceptor(
        metricsInterceptor
      )
      .prependInterceptor(correlationIdInterceptors)
      .options

  def toHttp[R](se: List[ZServerEndpoint[R, ZioStreams]]) =
    ZioHttpInterpreter(
      serverOptions(MetricsEndpoints.prometheusMetrics.metricsInterceptor())
    )
      .toApp(se)
  def toHttp[R](se: ZServerEndpoint[R, ZioStreams]) =
    ZioHttpInterpreter(
      serverOptions(MetricsEndpoints.prometheusMetrics.metricsInterceptor())
    )
      .toApp(se)

  def swagger(
      name: String,
      version: String,
      endpoints: List[Endpoint[
        Unit,
        _,
        Unit,
        _,
        Any
      ]]
  ) = SwaggerInterpreter()
    .fromEndpoints[Task](
      endpoints,
      name,
      version
    )
}
