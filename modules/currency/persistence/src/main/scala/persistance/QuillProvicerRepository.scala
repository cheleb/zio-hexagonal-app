package persistance

import currency.core.CurrencyRepository
import currency.core.Provider
import zio.*
import io.getquill.*
import javax.sql.DataSource
import currency.core.ProviderRepository
import java.lang.System

import currency.core.Provider
import currency.core.ProviderRepository
case class QuillProviderRepository(
    ctx: PostgresZioJdbcContext[SnakeCase],
    dataSource: DataSource
) extends ProviderRepository {

  import ctx.*

  val env = ZLayer.succeed(dataSource)

  override def store(currency: Provider): Task[Unit] =
    inline def q = quote {
      query[Provider].insertValue(lift(currency))
    }

    run(q).provide(env).unit <* ZIO.debug(s"Stored $currency")

  override def find(code: String): Task[Option[Provider]] =
    inline def q = quote {
      query[Provider].filter(_.id == lift(code))
    }

    ctx.run(q.value).provide(env)

}

object QuillProviderRepository {
  def live: ZLayer[DataSource, Nothing, ProviderRepository] =
    ZLayer.fromFunction { (dataSource: DataSource) =>
      QuillProviderRepository(new PostgresZioJdbcContext(SnakeCase), dataSource)
    }
}
