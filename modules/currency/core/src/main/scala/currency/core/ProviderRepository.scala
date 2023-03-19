package currency.core

import zio.*

trait ProviderRepository {
  def store(provider: Provider): Task[Unit]
  def find(code: String): Task[Option[Provider]]
}
