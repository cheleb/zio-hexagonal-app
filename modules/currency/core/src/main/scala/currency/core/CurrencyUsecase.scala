package currency.core

import zio.*

case class CurrencyUseCase(repo: CurrencyRepository) {

  def persist(currency: Currency): ZIO[Any, Throwable, Unit] =
    for {
      _ <- ZIO.logInfo(s"Persisting $currency")
      _ <- repo.store(currency)
      _ <- ZIO.logInfo(s"Persisted $currency")
    } yield ()

  def list: ZIO[Any, Throwable, List[Currency]] =
    for {
      _ <- ZIO.logInfo(s"Listing currencies")
      currencies <- repo.list
      _ <- ZIO.logInfo(s"Listed currencies")
    } yield currencies

  def find(code: CurrencyCode): ZIO[Any, Throwable, Option[Currency]] =
    for {
      _ <- ZIO.logInfo(s"Finding currency $code")
      currencies <- repo.find(code)
      _ <- ZIO.logInfo(s"Found currency $code")
    } yield currencies

}

object CurrencyUseCase {
  val live: ZLayer[CurrencyRepository, Nothing, CurrencyUseCase] =
    ZLayer.fromFunction(CurrencyUseCase(_))

  def persist(currency: Currency): ZIO[CurrencyUseCase, Throwable, Unit] =
    ZIO.serviceWithZIO[CurrencyUseCase](_.persist(currency))

  def list: ZIO[CurrencyUseCase, Throwable, List[Currency]] =
    ZIO.serviceWithZIO[CurrencyUseCase](_.list)

  def find(
      code: CurrencyCode
  ): ZIO[CurrencyUseCase, Throwable, Option[Currency]] =
    ZIO.serviceWithZIO[CurrencyUseCase](_.find(code))
}
