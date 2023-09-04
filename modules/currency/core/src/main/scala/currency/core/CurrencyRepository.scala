package currency.core

import zio.*
import Currency.CurrencyCode

trait CurrencyRepository {
  def store(currency: Currency): Task[Unit]
  def find(code: CurrencyCode): Task[Option[Currency]]
  def list: Task[List[Currency]]
}
