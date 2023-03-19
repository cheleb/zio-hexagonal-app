package currency.core

opaque type CurrencyCode = String

object CurrencyCode:
  def apply(code: String): CurrencyCode = code
  extension (code: CurrencyCode) def value: String = code

final case class Currency(code: CurrencyCode, name: String, symbol: String)
