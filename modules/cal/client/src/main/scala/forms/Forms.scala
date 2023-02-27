package forms

import com.raquo.laminar.api.L.*
import cal.views.CurrencyCode

import dev.cheleb.scalamigen.*
import be.doeraene.webcomponents.ui5.*

object Forms {
  inline given Form[CurrencyCode] with
    def render(
        variable: Var[CurrencyCode]
    ): HtmlElement =
      Input(
        _.showClearIcon := true,
        value <-- variable.signal.map(_.toString()),
        onInput.mapToValue.map(CurrencyCode.apply) --> variable.writer
      )
}
