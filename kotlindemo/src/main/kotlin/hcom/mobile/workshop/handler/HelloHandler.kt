package hcom.mobile.workshop.handler

import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

@Component
class HelloHandler {
    fun handleHelloWorld(request: ServerRequest): Mono<ServerResponse> =
        ok().contentType(TEXT_PLAIN)
            .syncBody("hello")

    fun handleEcho(request: ServerRequest): Mono<ServerResponse> =
        ok().contentType(TEXT_PLAIN)
            .body(request.bodyToMono(String::class.java), String::class.java)
}
