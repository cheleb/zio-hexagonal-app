package calui

import dev.cheleb.scalamigen.*
import dev.cheleb.scalamigen.Form.given
import org.scalajs.dom
import com.raquo.laminar.api.L.*
import magnolia1.*
import be.doeraene.webcomponents.ui5.SideNavigation
import be.doeraene.webcomponents.ui5.Icon

import cal.views.CurrencyView
import cal.views.CurrencyCode
import dev.cheleb.scalamigen.Editable
import be.doeraene.webcomponents.ui5.UList
import be.doeraene.webcomponents.ui5.Input
import be.doeraene.webcomponents.ui5.Button
import sttp.client3.FetchBackend
import sttp.client3._
import sttp.client3.ziojson.*

import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

import forms.Forms.given

val itemVar = Var(CurrencyView(CurrencyCode.apply("EUR"), "Euro", "€"))

// val currencyPairVar = Var(
//   CurrencyPair(CurrencyCode.apply("EUR"), CurrencyCode.apply("USD"))
// )

object App extends App {

  val backend = FetchBackend()

  val currencies = Var(List.empty[CurrencyView])

  val myApp =
    div(
      child <-- itemVar.signal.map { item =>
        div(
          s"$item"
        )
      },
      Form.renderVar(itemVar),
      Button(
        "Insert",
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
            .response(asJson[List[CurrencyView]])
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
