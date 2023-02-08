package ziohttp

import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.ztapir.ZServerEndpoint

import zio.Task

object MetricsEndpoints {

  val prometheusMetrics: PrometheusMetrics[Task] =
    PrometheusMetrics.default[Task]()
  val metricsEndpoint: ZServerEndpoint[Any, Any] =
    prometheusMetrics.metricsEndpoint

}
