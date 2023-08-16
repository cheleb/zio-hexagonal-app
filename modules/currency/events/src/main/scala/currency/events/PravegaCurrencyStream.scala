package currency.events

import currency.core.CurrencyStream
import currency.core.Currency
import zio.*
import zio.stream.*
import zio.pravega.PravegaStream
import zio.pravega.WriterSettingsBuilder
import io.pravega.client.stream.impl.UTF8StringSerializer

final class PravegaCurrencyStream(
    queue: Queue[Currency]
) extends CurrencyStream {

  override def publish(currency: Currency): Task[Boolean] =
    queue.offer(currency)
}

object PravegaCurrencyStream:

  val stringWriterSettings =
    WriterSettingsBuilder()
      .eventWriterConfigBuilder(_.enableLargeEvents(true))
      .withSerializer(new UTF8StringSerializer)

  def live: ZLayer[PravegaStream, Throwable, CurrencyStream] =
    ZLayer.fromZIO(
      for {
        _ <- ZIO.debug("PravegaCurrencyStream live")
        sink = PravegaStream
          .sink("currency", stringWriterSettings)
        queue <- Queue.unbounded[Currency]
        _ <- ZStream
          .fromQueue(queue)
          .map(_.toString)
          .run(sink)
          .fork
      } yield PravegaCurrencyStream(queue)
    )
