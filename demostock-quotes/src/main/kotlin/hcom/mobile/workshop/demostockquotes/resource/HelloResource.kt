package hcom.mobile.workshop.demostockquotes.resource

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloResource {

    @GetMapping("/hello-world")
    fun hello() = "hello"

}
