package currency.core

import io.github.iltotore.iron.{given, *}
import io.github.iltotore.iron.constraint.all.{given, *}

final case class CurrencyPair(
    from: CurrencyCode,
    to: CurrencyCode,
    rate: Double :| Positive
)

object CurrencyPair {
  def create(
      from: String,
      to: String,
      rate: Double
  ) =
    for
      positiveRate <- rate.refineEither[Positive]
      fromCurrencyCode = CurrencyCode(from)
      toCurrencyCode = CurrencyCode(to)
    yield CurrencyPair(fromCurrencyCode, toCurrencyCode, positiveRate.refine)

}
