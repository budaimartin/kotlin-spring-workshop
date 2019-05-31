package hcom.mobile.workshop.demostockquotes.domain

import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant

data class Quote(val ticker: String, val price: BigDecimal, val instant: Instant = Instant.now()) {

    companion object {
        private val MATH_CONTEXT = MathContext(2)
    }

    constructor(ticker: String, price: Double) : this(ticker, BigDecimal(price, MATH_CONTEXT))
}
