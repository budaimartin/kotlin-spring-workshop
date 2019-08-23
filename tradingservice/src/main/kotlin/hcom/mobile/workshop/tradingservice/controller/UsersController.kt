package hcom.mobile.workshop.tradingservice.controller

import hcom.mobile.workshop.tradingservice.TradingUserRepository
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class UsersController(val userRepository: TradingUserRepository) {

    @GetMapping("/users")
    fun getUsers() = userRepository.findAll()

    @GetMapping("/users/{userName:.+}")

    fun getUserByUserName(@PathVariable userName: String) = userRepository.findByUserName(userName)
            .switchIfEmpty(Mono.error(UserNotFoundException("User $userName not found! ¯\\_(ツ)_/¯")))

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(NOT_FOUND)
    fun handleUserNotFound(exception: UserNotFoundException) = exception.message

}


class UserNotFoundException(message: String) : Exception(message)
