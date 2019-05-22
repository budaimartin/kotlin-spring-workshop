package hcom.mobile.workshop.configuration

import hcom.mobile.workshop.domain.Quote
import hcom.mobile.workshop.generator.QuoteGenerator
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toFlux
import java.time.Duration

val routerBeanDefinitions = beans {
    bean("helloRouter") {
        router {
            accept(MediaType.TEXT_PLAIN).nest {
                GET("/hello-world") {
                    ok().contentType(MediaType.TEXT_PLAIN)
                        .syncBody("hello")
                }
                POST("/echo") {
                    ok().contentType(MediaType.TEXT_PLAIN)
                        .body(it.bodyToMono(String::class.java), String::class.java)
                }
            }
        }
    }
    bean("quotesRouter") {
        router {
            GET("/quotes").nest {
                accept(MediaType.APPLICATION_JSON) {
                    ok().contentType(MediaType.APPLICATION_JSON)
                        .body(
                            ref<QuoteGenerator>().fetchQuoteStream(Duration.ofMillis(0))
                                .take(it.queryParam("size")
                                    .map { Integer.parseInt(it) }
                                    .orElse(10))
                                .toFlux(),
                            Quote::class.java
                        )
                }
                accept(MediaType.APPLICATION_STREAM_JSON) {
                    ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                        .body(
                            ref<QuoteGenerator>().fetchQuoteStream(Duration.ofMillis(1000)).toFlux(),
                            Quote::class.java
                        )
                }
            }
        }
    }

}
