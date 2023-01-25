package core

import zio.*

import core.Currency
trait CurrencyRepository {
  def store(currency: Currency): Task[Unit]
  def find(code: CurrencyCode): Task[Option[Currency]]
  def list: Task[List[Currency]]
}
