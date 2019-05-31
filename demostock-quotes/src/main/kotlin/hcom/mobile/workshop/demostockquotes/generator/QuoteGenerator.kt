package hcom.mobile.workshop.demostockquotes.generator

import org.springframework.stereotype.Component
import hcom.mobile.workshop.demostockquotes.domain.Quote
import java.math.BigDecimal
import java.math.MathContext
import java.util.*


@Component
class QuoteGenerator {
    private val mathContext = MathContext(2)
    private val random = Random()
    private val prices = listOf(
            Quote("CTXS", 82.26),
            Quote("DELL", 32.26),
            Quote("GOOG", 62.26),
            Quote("MSFT", 72.26),
            Quote("ORCL", 92.26),
            Quote("RHT", 62.26),
            Quote("VMW", 72.26)
    )

    fun generateQuotes() = prices.map {
        val newPrice = it.price.multiply(BigDecimal(0.05 * this.random.nextDouble()), this.mathContext)
        Quote(it.ticker, newPrice, it.instant)
    }

    fun fetchQuotes(): Sequence<Quote> {
        //val quoteSequence = sequenceOf(*(generateQuotes().toTypedArray()))
        return sequence {
            while (true) {
                yieldAll(generateQuotes())
            }
        }

        //return sequence
    }
}