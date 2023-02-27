package cal.views

import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

final case class ErrorView(code: Int, message: String)

object ErrorView:
  given JsonCodec[ErrorView] = DeriveJsonCodec.gen[ErrorView]
