package hcom.mobile.workshop.demostockquotes.resource

import hcom.mobile.workshop.demostockquotes.generator.QuoteGenerator
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.reactive.flow.asPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router

@Configuration
class RouterConfiguration {

    @Autowired
    lateinit var qouteGenerator: QuoteGenerator

    @Bean
    fun mainRouter() = router {
        POST("/echo") {
            ok().body(it.bodyToMono<String>())
        }

        GET("/hello-world") {
            ok().syncBody("Hello")
        }

        GET("/quotes").nest {
            accept(MediaType.APPLICATION_JSON) {
                ok().body(qouteGenerator.fetchQuotes()
                        .take(Integer.valueOf(it.queryParam("size")
                                .orElse("5")))
                        .asPublisher())
            }
            accept(MediaType.APPLICATION_STREAM_JSON) {
                // FIXME gets stuck when called, see https://github.com/Kotlin/kotlinx.coroutines/issues/1324
                ok().body(qouteGenerator.fetchQuotes().asPublisher())
            }
        }

    }
}
