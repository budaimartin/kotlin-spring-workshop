package hcom.mobile.workshop.demostockquotes.resource

import hcom.mobile.workshop.demostockquotes.generator.QuoteGenerator
import kotlinx.coroutines.flow.take
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

class QuoteResource {

    @Autowired
    lateinit var quoteGenerator: QuoteGenerator

    @GetMapping("/quotes?size={size}", produces = ["application/json"])
    fun getQuotes(@RequestParam(required = false, name = "size", defaultValue = "10") size: Int) =
            quoteGenerator.fetchQuotes().take(size)

    @GetMapping("/quotes", produces = ["application/stream+json"])
    fun getQuotesStream() = quoteGenerator.fetchQuotes()
}
