package currency.core.ports

import zio.*
import currency.core.*
import currency.core.ports.*
import currency.core.Currency.CurrencyCode

trait CurrencyRepository {
  def store(currency: Currency): Task[Unit]
  def find(code: CurrencyCode): Task[Option[Currency]]
  def list: Task[List[Currency]]
}
