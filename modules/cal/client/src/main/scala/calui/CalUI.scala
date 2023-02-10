package calui

import dev.cheleb.scalamigen.*
import dev.cheleb.scalamigen.Form.given
import org.scalajs.dom
import com.raquo.laminar.api.L.*
import magnolia1.*
import be.doeraene.webcomponents.ui5.SideNavigation
import be.doeraene.webcomponents.ui5.Icon

import cal.Currency
import cal.CurrencyCode
import dev.cheleb.scalamigen.Editable
import be.doeraene.webcomponents.ui5.UList
import be.doeraene.webcomponents.ui5.Input
import be.doeraene.webcomponents.ui5.Button
import sttp.client3.FetchBackend
import sttp.client3._
import sttp.client3.jsoniter._

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import com.github.plokhotnyuk.jsoniter_scala.core.JsonReader
import com.github.plokhotnyuk.jsoniter_scala.core.JsonWriter

val itemVar = Var(Currency(CurrencyCode.apply("EUR"), "Euro", "€"))

object App extends App {

  given JsonValueCodec[CurrencyCode] =
    new JsonValueCodec[CurrencyCode] {
      def decodeValue(in: JsonReader, default: CurrencyCode): CurrencyCode =
        CurrencyCode(in.readString(""))

      def encodeValue(x: CurrencyCode, out: JsonWriter): Unit =
        out.writeVal(x.toString())

      val nullValue: CurrencyCode = null.asInstanceOf[CurrencyCode]
    }

  given codecCurrency: JsonValueCodec[Currency] = JsonCodecMaker.make
  val backend = FetchBackend()

  inline given Form[CurrencyCode] with
    def render(
        variable: Var[CurrencyCode]
    ): HtmlElement =
      Input(
        _.showClearIcon := true,
        value <-- variable.signal.map(_.toString()),
        onInput.mapToValue.map(CurrencyCode.apply) --> variable.writer
      )

  val myApp =
    div(
      child <-- itemVar.signal.map { item =>
        div(
          s"$item"
        )
      },
      Form.renderVar(itemVar),
      Button(
        "Edit",
        onClick --> { _ =>
          println(itemVar.now())
          basicRequest
            .post(uri"http://localhost:8888/currency")
            .body(itemVar.now())
            .send(backend)

//        itemVar.update(_.copy(code = CurrencyCode.apply("USD")))
        }
      )
    )

  val containerNode = dom.document.getElementById("root")
  render(containerNode, myApp)
}
