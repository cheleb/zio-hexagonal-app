package core

import zio.*

import core.Currency
trait ProviderRepository {
  def store(provider: Provider): Task[Unit]
  def find(code: String): Task[Option[Provider]]
}
