package persistance

import core.CurrencyRepository
import core.Currency
import zio.*
import io.getquill.*
import javax.sql.DataSource

import persistance.QuillCurrencyRepository
import java.lang.System
case class QuillCurrencyRepository(
    ctx: PostgresZioJdbcContext[SnakeCase],
    dataSource: DataSource
) extends CurrencyRepository {

  import ctx.*

  val env = ZLayer.succeed(dataSource)

  override def store(currency: Currency): Task[Unit] =
    inline def q = quote {
      query[Currency].insertValue(lift(currency))
    }

    run(q).provide(env).unit <* ZIO.debug(s"Stored $currency")

  override def find(code: String): Task[Option[Currency]] =
    inline def q = quote {
      query[Currency].filter(_.code == lift(code))
    }

    ctx.run(q.value).provide(env)

}

object QuillCurrencyRepository {
  def live: ZLayer[DataSource, Nothing, CurrencyRepository] =
    ZLayer.fromFunction { (dataSource: DataSource) =>
      println(System.identityHashCode(dataSource))
      QuillCurrencyRepository(new PostgresZioJdbcContext(SnakeCase), dataSource)
    }
}
