package calui

import dev.cheleb.scalamigen.*
import dev.cheleb.scalamigen.Form.given
import org.scalajs.dom
import com.raquo.laminar.api.L.*
import magnolia1.*
import be.doeraene.webcomponents.ui5.SideNavigation
import be.doeraene.webcomponents.ui5.Icon

// Define some models
case class Currency(
    code: String,
    name: String,
    symbol: String
)

val itemVar = Var(Currency("EUR", "Euro", "€"))

object App extends App {

  val myApp =
    div(
      Form.renderVar(itemVar)
    )

  val containerNode = dom.document.getElementById("root")
  render(containerNode, myApp)
}
