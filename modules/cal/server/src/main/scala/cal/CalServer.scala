package cal

import zio.*
import zio.http.*
import zio.http.socket.{WebSocketChannelEvent, WebSocketFrame}

import zio.http.Method
import zio.stream.ZStream
import zio.http.Headers

import zio.http.Path.Segment
import zio.http.Path.Segment.Root
import zio.http.ChannelEvent.ChannelRead
import ziohttp.ZIOHttp

object HH
// def contentType(path: Path): Headers =
//   path.lastSegment
//     .map(p => p.text.substring(p.text.lastIndexOf('.') + 1))
//     .map(ext => Headers.contentType(s"image/$ext"))
//     .getOrElse(Headers.empty)

object CalServer extends ZIOAppDefault {

  val static = Http.collectHttp[Request] {
    case Method.GET -> !! =>
      Http
        .fromResource("public/index.html")
    // // .addHeaders(Headers.contentType("text/html"))
    case Method.GET -> "" /: "images" /: path =>
      Handler
        .fromStream(ZStream.fromResource(s"public/images/$path"))
//        .addHeaders(HH.contentType(path))
        .toHttp
    case Method.GET -> !! / "css" =>
      Handler.fromStream(ZStream.fromResource("public/style.css")).toHttp

    case Method.GET -> "" /: "images" /: path =>
      Handler.text(s"Path: $path").toHttp
    case Method.GET -> !! / "favicon.ico" =>
      Handler.fromStream(ZStream.fromResource("public/favicon.ico")).toHttp
  }

  private val socket =
    Http.collectZIO[WebSocketChannelEvent] {
      case ChannelEvent(ch, ChannelRead(WebSocketFrame.Ping)) =>
        ch.writeAndFlush(WebSocketFrame.Pong)

      case ChannelEvent(ch, ChannelRead(WebSocketFrame.Pong)) =>
        ch.writeAndFlush(WebSocketFrame.Ping)

      case ChannelEvent(ch, ChannelRead(WebSocketFrame.Text(text))) =>
        ch.write(WebSocketFrame.text(text)).repeatN(10) *> ch.flush
    }

  val ws = Http.collectZIO[Request] {
    case Method.GET -> !! / "greet" / name =>
      ZIO.succeed(Response.text(s"Greetings {$name}!"))
    case Method.GET -> !! / "subscriptions" =>
      socket.toSocketApp.toResponse
  }

  val dynamic = Http.collectZIO[Request] {
    case Method.GET -> "" /: "hello" /: name =>
      ZIO.succeed(Response.text(s"Hello, $name!"))
  }

  val statics2 = ZIOHttp.toHttp(CalEndpoints.all)

  val app = statics2 ++ dynamic ++ static ++ ws

  val config = Server.Config.default
    .port(8888)
//    .leakDetection(LeakDetectionLevel.PARANOID)
//    .maxThreads(nThreads)
  val configLayer = ZLayer.succeed(config)

  override val run =
    Server
      .serve(app.withDefaultErrorResponse)
      .provide(configLayer, Server.live)
}
