package currency.service

import flywayutil.DatabaseConfig

import zio.config._, zio.config.typesafe._
import zio.config.magnolia.descriptor
import com.typesafe.config.Config

final case class AppConfig(database: DatabaseConfig)

object AppConfig {
  val configDescriptor: ConfigDescriptor[AppConfig] =
    descriptor[AppConfig]

  def load = read(configDescriptor.from(ConfigSource.fromResourcePath))

}
