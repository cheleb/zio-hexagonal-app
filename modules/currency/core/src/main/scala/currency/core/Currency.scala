package currency.core

import Currency.CurrencyCode

final case class Currency(code: CurrencyCode, name: String, symbol: String)

object Currency:

  opaque type CurrencyCode = String

  object CurrencyCode:
    def apply(code: String): CurrencyCode = code
    extension (code: CurrencyCode) def value: String = code
