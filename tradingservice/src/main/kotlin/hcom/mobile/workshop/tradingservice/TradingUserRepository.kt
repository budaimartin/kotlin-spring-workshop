package hcom.mobile.workshop.tradingservice

import hcom.mobile.workshop.tradingservice.domain.TradingUser
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface TradingUserRepository : ReactiveMongoRepository<TradingUser, String> {
    fun findByUserName(name: String): TradingUser
}
