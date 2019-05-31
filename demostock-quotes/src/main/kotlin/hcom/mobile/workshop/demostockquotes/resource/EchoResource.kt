package hcom.mobile.workshop.demostockquotes.resource

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EchoResource {

    @PostMapping("/echo")
    fun echo(@RequestBody input: String) = input
}
