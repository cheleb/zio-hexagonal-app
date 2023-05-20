package calui

import dev.cheleb.scalamigen.{*, given}
import org.scalajs.dom
import com.raquo.laminar.api.L.*
import magnolia1.*
import be.doeraene.webcomponents.ui5.SideNavigation
import be.doeraene.webcomponents.ui5.Icon

import cal.views.*

import be.doeraene.webcomponents.ui5.UList
import be.doeraene.webcomponents.ui5.Input
import be.doeraene.webcomponents.ui5.Button

import io.github.iltotore.iron.{given, *}
import io.github.iltotore.iron.constraint.all.{given, *}

import sttp.client3.FetchBackend
import sttp.client3._
import sttp.client3.ziojson.*

import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

import dev.cheleb.scalamigen.Defaultable

case class Address(street: String, city: String, country: String)

case class Person(name: String, age: Int, address: Option[Address])

val itemVar = Var(CurrencyView(CurrencyCode.apply("EUR"), "Euro", "€"))

val currencyPairVar = Var(
  CurrencyPairView(CurrencyCode.apply("EUR"), CurrencyCode.apply("USD"), 1.0)
)

given Defaultable[Address] with
  def default = Address("", "", "")

given Form[CurrencyCode] = stringForm(CurrencyCode.apply)

object App extends App {

  val backend = FetchBackend()

  val currencies = Var(List.empty[CurrencyView])

  val myApp =
    div(
      div(child <-- itemVar.signal.map { item =>
        div(
          s"$item"
        )
      }),
      div(
        child <-- currencyPairVar.signal.map { item =>
          div(
            s"$item"
          )
        }
      ),
      Form.renderVar(currencyPairVar),
      Form.renderVar(itemVar),
      Button(
        "Insert",
        onClick --> { _ =>
          println(itemVar.now())
          basicRequest
            .post(uri"http://localhost:8888/currency")
            .body(itemVar.now())
            .send(backend)

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
