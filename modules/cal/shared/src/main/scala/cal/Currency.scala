package cal

opaque type CurrencyCode = String

object CurrencyCode:
  def apply(code: String): CurrencyCode = code

final case class Currency(code: CurrencyCode, name: String, symbol: String)
