package hcom.mobile.workshop.tradingservice

import hcom.mobile.workshop.tradingservice.domain.TradingUser
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class UserInitialiserListener(val userRepository: TradingUserRepository) : ApplicationListener<ApplicationReadyEvent> {
    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        userRepository.saveAll(
                listOf(TradingUser("Joco", "Pocok Joco"),
                        TradingUser("Joci", "Poci Joci")))
                .doOnNext { println(it) }
                .subscribe()
    }
}
