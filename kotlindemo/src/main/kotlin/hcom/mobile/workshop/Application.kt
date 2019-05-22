package hcom.mobile.workshop

import hcom.mobile.workshop.configuration.routerBeanDefinitions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
open class QuotesApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .initializers(routerBeanDefinitions)
        .sources(QuotesApplication::class.java)
        .run(*args)
}
