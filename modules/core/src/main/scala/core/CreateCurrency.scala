package core

import zio.*

import core.CurrencyRepository
import core.CreateCurrency
import core.Currency
case class CreateCurrency(repo: CurrencyRepository) {
  def create(name: String, symbol: String, code: String): Currency =
    Currency(name, symbol, code)

  def persist(currency: Currency): ZIO[Any, Throwable, Unit] =
    for {
      _ <- ZIO.succeed(println(s"Persisting $currency"))
      _ <- repo.store(currency)
      _ <- ZIO.succeed(println(s"Persisted $currency"))
    } yield ()

}

object CreateCurrency {
  val live: ZLayer[CurrencyRepository, Nothing, CreateCurrency] =
    ZLayer.fromFunction(CreateCurrency(_))

  def persist(currency: Currency): ZIO[CreateCurrency, Throwable, Unit] =
    ZIO.serviceWithZIO[CreateCurrency](_.persist(currency))
}
