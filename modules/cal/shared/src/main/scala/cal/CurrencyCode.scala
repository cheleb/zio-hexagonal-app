package cal

opaque type CurrencyCode = String

object CurrencyCode:
  def apply(code: String): CurrencyCode = code
