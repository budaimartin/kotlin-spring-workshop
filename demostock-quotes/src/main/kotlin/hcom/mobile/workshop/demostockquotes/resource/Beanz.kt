package hcom.mobile.workshop.demostockquotes.resource

import hcom.mobile.workshop.demostockquotes.generator.QuoteGenerator
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.reactive.flow.asPublisher
import org.springframework.context.support.beans
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router

fun beanz() = beans {
    bean<QuoteGenerator>()
    bean {
        val generator = ref<QuoteGenerator>()
        router {
            POST("/echo") {
                ok().body(it.bodyToMono<String>())
            }

            GET("/hello-world") {
                ok().syncBody("Hello")
            }

            GET("/quotes").nest {
                accept(MediaType.APPLICATION_JSON) {
                    ok().body(generator.fetchQuotes()
                            .take(Integer.valueOf(it.queryParam("size")
                                    .orElse("5")))
                            .asPublisher())
                }
                accept(MediaType.APPLICATION_STREAM_JSON) {
                    ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                            .body(generator.fetchQuotes().asPublisher())
                }
            }
        }
    }
}
