package hcom.mobile.workshop

import hcom.mobile.workshop.configuration.routerBeanDefinitions
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.stereotype.Component

@SpringBootApplication
open class QuotesApplication

fun main(args: Array<String>) {
    SpringApplication.run(QuotesApplication::class.java, *args)
}
