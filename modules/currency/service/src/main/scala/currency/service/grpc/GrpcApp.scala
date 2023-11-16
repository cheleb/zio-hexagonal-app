package currency.service.grpc

import zio.ZLayer

import scalapb.zio_grpc.{Server => GrpcServer}
import scalapb.zio_grpc.ServiceList
import scalapb.zio_grpc.ServerLayer
import io.grpc.ServerBuilder
import currency.core.usecases.CurrencyUseCase

object GrpcApp {

  def server: ZLayer[CurrencyUseCase, Throwable, GrpcServer] =
    val serviceList =
      ServiceList.addFromEnvironment[CurrencyService]
    val serverLayer =
      ServerLayer.fromServiceList(
        ServerBuilder.forPort(9000),
        serviceList
      )

    CurrencyService.live >>>
      ZLayer.makeSome[CurrencyService, GrpcServer](
        serverLayer
      )
}
