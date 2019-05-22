package hcom.mobile.workshop.generator

import hcom.mobile.workshop.domain.Quote
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.MathContext
import java.time.Duration
import java.util.*


@Component
class QuoteGenerator {

    private val mathContext = MathContext(2)
    private val random = Random()
    private val prices = mutableListOf(
        Quote("CTXS", 82.26),
        Quote("DELL", 63.74),
        Quote("GOOG", 847.24),
        Quote("MSFT", 65.11),
        Quote("ORCL", 45.71),
        Quote("RHT", 84.29),
        Quote("VMW", 92.21)
    )

    fun fetchQuoteStream(period: Duration) = sequence {
        while (true) {
            runBlocking {
                delay(period.toMillis())
            }
            yieldAll(generateQuotes())
        }
    }

    private fun generateQuotes() = prices.map {
        val priceChange = it.price.multiply(BigDecimal(0.05 * random.nextDouble(), mathContext))
        Quote(it.ticker, it.price.add(priceChange))
    }
}
