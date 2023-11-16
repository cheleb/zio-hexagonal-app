package currency.service.grpc

import co.ledger.cal.currencies.grpc.currency.*
import io.grpc.Status
import zio._
import currency.core.usecases.CurrencyUseCase
import scalapb.zio_grpc.RequestContext

class CurrencyService(currencyUseCase: CurrencyUseCase)
    extends ZioCurrency.CurrencyRepository {

  override def getCurrencies(
      request: CurrenciesRequest
  ): ZIO[Any, Status, CurrenciesResponse] =
    for currencies <- currencyUseCase.list.mapError(_ => Status.INTERNAL)
    yield CurrenciesResponse(
      currencies.map(c => Currency(c.name, c.code.value, c.symbol))
    )

}

object CurrencyService:

  def live: ZLayer[CurrencyUseCase, Nothing, CurrencyService] =
    ZLayer.fromFunction(new CurrencyService(_))
