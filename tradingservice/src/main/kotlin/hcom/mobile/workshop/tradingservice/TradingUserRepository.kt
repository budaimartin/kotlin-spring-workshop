package hcom.mobile.workshop.tradingservice

import hcom.mobile.workshop.tradingservice.domain.TradingUser
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface TradingUserRepository : ReactiveMongoRepository<TradingUser, String> {
    fun findByUserName(userName: String): Mono<TradingUser>
}
