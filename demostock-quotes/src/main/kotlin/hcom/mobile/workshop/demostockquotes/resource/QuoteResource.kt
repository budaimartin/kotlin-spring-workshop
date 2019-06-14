package hcom.mobile.workshop.demostockquotes.resource

import hcom.mobile.workshop.demostockquotes.generator.QuoteGenerator
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class QuoteResource {

    @Autowired
    lateinit var quoteGenerator: QuoteGenerator

    @GetMapping("/quotes", produces = ["application/json"])
    fun getQuotes(size: Int = 10) = quoteGenerator.fetchQuotes().onEach { println(it) }.take(size)
}
