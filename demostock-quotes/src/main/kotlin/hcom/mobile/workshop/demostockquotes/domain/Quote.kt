package hcom.mobile.workshop.demostockquotes.domain

import java.time.Instant
import java.math.BigDecimal
import java.math.MathContext

data class Quote(val ticker: String, val price: BigDecimal) {
    lateinit var instant: Instant

    companion object {
        private val MATH_CONTEXT = MathContext(2)
    }


    // TODO: constructor(ticker: String, price: Double)
    // TODO: what about equals hashcode? (because of lateinit var instant: Instant)
}