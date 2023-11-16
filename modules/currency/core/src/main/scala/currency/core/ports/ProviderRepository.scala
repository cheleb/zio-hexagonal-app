package currency.core.ports

import zio.*

import currency.core.*

trait ProviderRepository {
  def store(provider: Provider): Task[Unit]
  def find(code: String): Task[Option[Provider]]
}
