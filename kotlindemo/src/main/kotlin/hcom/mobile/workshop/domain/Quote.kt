package hcom.mobile.workshop.domain

import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant

class Quote(val ticker: String, val price: BigDecimal) {
    var instant = Instant.now()

    constructor(ticker: String, price: Double) : this(ticker, BigDecimal(price, MathContext(2)))

    override fun toString() = "Quote[ticker=$ticker, price=$price, instant = $instant]"


}
