package core

import zio.*

import core.Currency
trait CurrencyRepository {
  def store(currency: Currency): Task[Unit]
  def find(code: String): Task[Option[Currency]]
}
