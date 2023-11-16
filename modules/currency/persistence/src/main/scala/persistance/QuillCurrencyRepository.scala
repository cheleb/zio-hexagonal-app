package persistance

import currency.core.*
import zio.*
import io.getquill.*
import javax.sql.DataSource

import persistance.QuillCurrencyRepository
import java.lang.System
import currency.core.Currency.CurrencyCode
import currency.core.ports.CurrencyRepository

case class QuillCurrencyRepository(
    ctx: PostgresZioJdbcContext[SnakeCase],
    dataSource: DataSource
) extends CurrencyRepository {

  import ctx.*

  given MappedEncoding[CurrencyCode, String] =
    MappedEncoding[CurrencyCode, String](_.toString)
  given MappedEncoding[String, CurrencyCode] =
    MappedEncoding[String, CurrencyCode](CurrencyCode(_))

  val env = ZLayer.succeed(dataSource)

  override def store(currency: Currency): Task[Unit] =
    inline def q = quote {
      query[Currency].insertValue(lift(currency))
    }

    run(q).provide(env).unit <* ZIO.debug(s"Stored $currency")

  override def find(code: CurrencyCode): Task[Option[Currency]] =
    inline def q = quote {
      query[Currency].filter(_.code == lift(code))
    }

    ctx.run(q.value).provide(env)

  override def list: Task[List[Currency]] =
    inline def q = quote {
      query[Currency]
    }
    ctx.run(q).provide(env)
}

object QuillCurrencyRepository {
  def live: ZLayer[DataSource, Nothing, CurrencyRepository] =
    ZLayer.fromFunction { (dataSource: DataSource) =>
      QuillCurrencyRepository(new PostgresZioJdbcContext(SnakeCase), dataSource)
    }
}
