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
    @GetMapping(path = ["/quotes/feed"],  produces = ["text/event-stream"])
    fun quotesFeed(): Flux<Quote> {

        val webclient = WebClient.create("http://localhost:8081")


        return webclient.get()
                .uri("/quotes")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux()
    }
}
