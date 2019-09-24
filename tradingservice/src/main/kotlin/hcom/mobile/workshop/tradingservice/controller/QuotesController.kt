package hcom.mobile.workshop.tradingservice.controller

import hcom.mobile.workshop.tradingservice.domain.Quote
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Flux

@Controller
class QuotesController {
    @GetMapping("/quotes")
    fun quotes() = "quotes"
}

@RestController
class QuotesFeedController() {
    @GetMapping("/quotes/feed")
    fun quotesFeed(): Flux<Quote> {

        val webclient = WebClient.builder()
                .baseUrl("localhost:8081")
                .build()
        return webclient.get()
                .uri("/quotes")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux()
    }
}
