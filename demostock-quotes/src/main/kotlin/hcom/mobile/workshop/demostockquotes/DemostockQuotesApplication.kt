package hcom.mobile.workshop.demostockquotes

import hcom.mobile.workshop.demostockquotes.domain.Quote
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.math.BigDecimal

@SpringBootApplication
class DemostockQuotesApplication

fun main(args: Array<String>) {
	runApplication<DemostockQuotesApplication>(*args)
}
