package cal.views

import zio.json.JsonCodec
import zio.json.DeriveJsonCodec

final case class Error(code: Int, message: String)

object Error:
  given JsonCodec[Error] = DeriveJsonCodec.gen[Error]
