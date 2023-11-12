package currency.service

import flywayutil.DatabaseConfig

import zio.config._, zio.config.typesafe._
import zio.config.magnolia.descriptor
import com.typesafe.config.Config

import config.PravegaConfig
import zio.ZIO

/** Application configuration
  *
  * @param database
  * @param pravega
  */
final case class AppConfig(database: DatabaseConfig, pravega: PravegaConfig)

object AppConfig {
  val configDescriptor: ConfigDescriptor[AppConfig] =
    descriptor[AppConfig]

  def load: ZIO[Any, ReadError[String], AppConfig] = read(
    configDescriptor.from(ConfigSource.fromResourcePath)
  )

}
