package currency.service.config

import java.net.URI
import zio.config._, zio.config.typesafe._
import zio.config.magnolia.descriptor

final case class PravegaConfig(
    controllerIP: String
) {
  def controllerURI: URI = URI.create(s"tcp://$controllerIP:9090")
}

object PravegaConfig {

  implicit val uriDescriptor: ConfigDescriptor[URI] =
    ConfigDescriptor.string.map(URI.create)

  val configDescriptor: ConfigDescriptor[PravegaConfig] =
    descriptor[PravegaConfig]

}
