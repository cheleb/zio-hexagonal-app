package cal.views

import io.github.iltotore.iron.{given, *}
import io.github.iltotore.iron.constraint.all.{given, *}

final case class CurrencyPairView(
    from: CurrencyCode,
    to: CurrencyCode,
    rate: Double :| Positive
)

object CurrencyPairView {
  def create(
      from: String,
      to: String,
      rate: Double
  ) =
    for
      positiveRate <- rate.refineEither[Positive]
      fromCurrencyCode = CurrencyCode(from)
      toCurrencyCode = CurrencyCode(to)
    yield CurrencyPairView(
      fromCurrencyCode,
      toCurrencyCode,
      positiveRate.refine
    )

}
