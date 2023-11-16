package currency.core.ports

import zio.*

import currency.core.Currency

trait CurrencyStream {
  def publish(currency: Currency): Task[Boolean]
}
