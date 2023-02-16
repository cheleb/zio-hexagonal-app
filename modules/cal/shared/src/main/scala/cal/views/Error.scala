package cal.views

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker

final case class Error(code: Int, message: String)

object Error:
  given JsonValueCodec[Error] = JsonCodecMaker.make
