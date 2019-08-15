package hcom.mobile.workshop.tradingservice.controller

import hcom.mobile.workshop.tradingservice.TradingUserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UsersController(val userRepository: TradingUserRepository) {

    @GetMapping("/users")
    fun getUsers() = userRepository.findAll()

    @GetMapping("/users/{userName:.+}")
    fun getUserByUserName(@PathVariable userName: String) = userRepository.findByUserName(userName)
            .switchIfEmpty(Mono.error(Exception("Nincs Joco :(")))

}
