package forms

import com.raquo.laminar.api.L.*
import cal.views.CurrencyCode

import dev.cheleb.scalamigen.*
import be.doeraene.webcomponents.ui5.*

import io.github.iltotore.iron.{given, *}
import io.github.iltotore.iron.constraint.all.{given, *}
import scala.compiletime.ops.double
import scala.util.Try
import scala.util.Failure
import scala.util.Success

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

  // inline given Form[IronType[Double, Positive]] with
  //   def render(
  //       variable: Var[IronType[Double, Positive]]
  //   ): HtmlElement =
  //     val errorVar = Var("")
  //     div(
  //       div(child <-- errorVar.signal.map { item =>
  //         div(
  //           s"$item"
  //         )
  //       }),
  //       Input(
  //         _.showClearIcon := true,
  //         backgroundColor <-- errorVar.signal.map {
  //           case "" => "white"
  //           case _  => "red"
  //         },
  //         value <-- variable.signal.map(_.toString()),
  //         onInput.mapToValue
  //           .filter { str =>
  //             str.toDoubleOption match
  //               case None =>
  //                 errorVar.set("Not a number")
  //                 false
  //               case Some(double) =>
  //                 double.refineEither[Positive] match
  //                   case Left(error) =>
  //                     errorVar.set(error)
  //                     false
  //                   case Right(value) =>
  //                     errorVar.set("")
  //                     false
  //           }
  //           .map { case str =>
  //             str.toDouble.refine[Positive]
  //           } --> variable.writer
  //       )
  //     )
}
