import core.CreateCurrency
import core.Currency
import zio.*

import core.*
import persistance.QuillCurrencyRepository
import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.jdbczio.Quill.DataSource

object App extends ZIOAppDefault {

  val program = for {
    _ <- ZIO.succeed(println("Hello World!"))
  } yield ()
  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] =
    CreateCurrency
      .persist(Currency("Dollar", "$", "USD"))
      .provide(
        CreateCurrency.live,
        QuillCurrencyRepository.live,
        DataSource.fromPrefixClosable("db")
//,        ZLayer.Debug.tree
      )

}
