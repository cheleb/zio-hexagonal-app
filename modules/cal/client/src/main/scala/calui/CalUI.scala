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
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

val itemVar = Var(Currency(CurrencyCode.apply("EUR"), "Euro", "€"))

object App extends App {

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

  val currencies = Var(List.empty[Currency])

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
      ),
      ul(
        children <-- currencies.signal.map { items =>
          items.map { item =>
            li(
              s"$item"
            )
          }
        }
      ),
      Button(
        "Refresh list",
        onClick --> { _ =>
          basicRequest
            .get(uri"http://localhost:8888/currency")
            .response(asJson[List[Currency]])
            .send(backend)
            .onComplete {
              case Failure(error) => println(error)
              case Success(response) =>
                response.body match {
                  case Left(error)  => println(error)
                  case Right(value) => currencies.update(_ => value)
                }

            }

//        itemVar.update(_.copy(code = CurrencyCode.apply("USD")))
        }
      )
    )

  val containerNode = dom.document.getElementById("root")
  render(containerNode, myApp)
}
