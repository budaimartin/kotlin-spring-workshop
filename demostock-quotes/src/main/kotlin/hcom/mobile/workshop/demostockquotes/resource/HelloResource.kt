package hcom.mobile.workshop.demostockquotes.resource

import hcom.mobile.workshop.demostockquotes.domain.Quote
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.Instant

@RestController
class HelloResource {

    @GetMapping("/hello-world")
    fun hello() = "hello"

}