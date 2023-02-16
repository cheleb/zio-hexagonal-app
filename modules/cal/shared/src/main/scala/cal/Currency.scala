package cal

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import com.github.plokhotnyuk.jsoniter_scala.core.JsonReader
import com.github.plokhotnyuk.jsoniter_scala.core.JsonWriter

opaque type CurrencyCode = String

object CurrencyCode:
  def apply(code: String): CurrencyCode = code

final case class Currency(code: CurrencyCode, name: String, symbol: String)

object Currency:
  given JsonValueCodec[CurrencyCode] =
    new JsonValueCodec[CurrencyCode] {
      def decodeValue(in: JsonReader, default: CurrencyCode): CurrencyCode =
        CurrencyCode(in.readString(""))

      def encodeValue(x: CurrencyCode, out: JsonWriter): Unit =
        out.writeVal(x.toString())

      val nullValue: CurrencyCode = null.asInstanceOf[CurrencyCode]
    }

  given codecCurrency: JsonValueCodec[Currency] = JsonCodecMaker.make
