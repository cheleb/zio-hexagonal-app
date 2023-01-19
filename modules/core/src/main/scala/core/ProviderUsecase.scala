package core

import zio.*

import core.Currency

class ProviderUsecase(repo: ProviderRepository) {
  def create(name: String, symbol: String, code: String): Currency =
    Currency(name, symbol, code)

  def persist(provider: Provider): ZIO[Any, Throwable, Unit] =
    for {
      _ <- ZIO.succeed(println(s"Persisting $provider"))
      _ <- repo.store(provider)
      _ <- ZIO.succeed(println(s"Persisted $provider"))
    } yield ()

}

object ProviderUsecase {
  val live: ZLayer[ProviderRepository, Nothing, ProviderUsecase] =
    ZLayer.fromFunction(ProviderUsecase(_))

  def persist(provider: Provider): ZIO[ProviderUsecase, Throwable, Unit] =
    ZIO.serviceWithZIO[ProviderUsecase](_.persist(provider))
}
