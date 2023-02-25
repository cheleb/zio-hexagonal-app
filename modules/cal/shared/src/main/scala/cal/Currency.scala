package cal

import zio.json.JsonDecoder
import zio.json.JsonEncoder
import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

opaque type CurrencyCode = String

object CurrencyCode:
  def apply(code: String): CurrencyCode = code

final case class Currency(code: CurrencyCode, name: String, symbol: String)

object Currency:
  given JsonDecoder[CurrencyCode] =
    JsonDecoder[String].map(CurrencyCode.apply)
  given JsonEncoder[CurrencyCode] =
    JsonEncoder[String].contramap(_.toString())
  given JsonCodec[Currency] = DeriveJsonCodec.gen[Currency]
