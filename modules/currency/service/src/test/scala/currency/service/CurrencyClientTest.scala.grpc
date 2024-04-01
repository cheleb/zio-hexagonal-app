package currency.service

import zio._

import co.ledger.cal.currencies.grpc.currency.ZioCurrency.CurrencyRepositoryClient
import co.ledger.cal.currencies.grpc.currency.CurrenciesRequest
import scalapb.zio_grpc.ZManagedChannel
import io.grpc.ManagedChannelBuilder

object CurrencyClientTest extends ZIOAppDefault {

  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] =
    CurrencyRepositoryClient
      .getCurrencies(CurrenciesRequest())
      .provide(
        CurrencyRepositoryClient.live(
          ZManagedChannel(
            ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext()
          )
        )
      )
      .map(println)
}
