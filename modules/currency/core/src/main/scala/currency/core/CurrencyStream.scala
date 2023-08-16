package currency.core

import zio.*

trait CurrencyStream {
  def publish(currency: Currency): Task[Boolean]
}
