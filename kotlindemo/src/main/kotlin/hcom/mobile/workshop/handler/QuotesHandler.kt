package hcom.mobile.workshop.handler

import hcom.mobile.workshop.domain.Quote
import hcom.mobile.workshop.generator.QuoteGenerator
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_STREAM_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import java.time.Duration

@Component
class QuotesHandler(private val quoteGenerator: QuoteGenerator) {
    fun handleQuotes(request: ServerRequest): Mono<ServerResponse> =
        ok().contentType(APPLICATION_JSON)
            .body(
                quoteGenerator.fetchQuoteStream(Duration.ofMillis(0))
                    .take(request.queryParam("size")
                        .map { Integer.parseInt(it) }
                        .orElse(10))
                    .toFlux(),
                Quote::class.java
            )

    fun handleQuotesStream(request: ServerRequest): Mono<ServerResponse> =
        ok().contentType(APPLICATION_STREAM_JSON)
            .body(
                quoteGenerator.fetchQuoteStream(Duration.ofMillis(1000)).toFlux(),
                Quote::class.java
            )
}
