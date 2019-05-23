package hcom.mobile.workshop.configuration

import hcom.mobile.workshop.handler.HelloHandler
import hcom.mobile.workshop.handler.QuotesHandler
import org.springframework.context.support.beans
import org.springframework.http.MediaType.*
import org.springframework.web.reactive.function.server.router

val routerBeanDefinitions = beans {
    bean("helloRouter") {
        router {
            accept(TEXT_PLAIN).nest {
                GET("/hello-world") { ref<HelloHandler>().handleHelloWorld(it) }
                POST("/echo") { ref<HelloHandler>().handleEcho(it) }
            }
        }
    }
    bean("quotesRouter") {
        router {
            GET("/quotes").nest {
                accept(APPLICATION_JSON) { ref<QuotesHandler>().handleQuotes(it) }
                accept(APPLICATION_STREAM_JSON) { ref<QuotesHandler>().handleQuotesStream(it) }
            }
        }
    }

}
