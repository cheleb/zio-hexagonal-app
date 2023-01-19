package flywayutil

import zio.*
import org.flywaydb.core.Flyway
import com.typesafe.config.Config
import com.typesafe.config.ConfigUtil
import com.typesafe.config.ConfigFactory

final case class DatabaseConfig(
    host: String,
    port: Int = 5432,
    dbname: String,
    username: String,
    password: String
) {
  def endpoint = s"$host:$port"
  def url = s"jdbc:postgresql://$endpoint/$dbname"
  def asConfig: Config =
    import scala.jdk.CollectionConverters.*
    ConfigFactory.parseMap(
      Map(
        "dataSourceClassName" -> "org.postgresql.ds.PGSimpleDataSource",
        "dataSource.databaseName" -> dbname,
        "jdbcUrl" -> url,
        "username" -> username,
        "password" -> password
      ).asJava
    )
}

object FlywayMigration {

  def migrate(config: DatabaseConfig): Task[Unit] =

    val flyway = Flyway
      .configure()
      .baselineVersion("0.0.0")
      .baselineOnMigrate(true)
      .dataSource(config.url, config.username, config.password)
      .locations("classpath:db/migrations")
      // - missing: not needed for projects that do not use repeatable migrations, would rather use
      // "repeatable:missing", but ignoreMigrationPattern with type 'repeatable' is not supported by Flyway
      // Community Edition
      // - future: required since Flyway 9.x, otherwise rollbacking the application would fail to start
      .ignoreMigrationPatterns("*:missing", "*:future")
      .load()

    for
      _ <- ZIO.logInfo(s"Migrating schema on ${config.endpoint}")
      _ <- ZIO.attempt(flyway.migrate())
      _ <- ZIO.logInfo(s"Migrated schema on ${config.endpoint}")
    yield ()
}
