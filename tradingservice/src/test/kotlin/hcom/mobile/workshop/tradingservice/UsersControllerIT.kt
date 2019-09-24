package hcom.mobile.workshop.tradingservice

import hcom.mobile.workshop.tradingservice.domain.TradingUser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner::class)
class UsersControllerIT {

    @Autowired
    lateinit var tradingUserRepository: TradingUserRepository
    @Autowired
    lateinit var webTestClient: WebTestClient
    lateinit var tradingUser: TradingUser

    @Before
    fun init() {
        tradingUser = tradingUserRepository.save(TradingUser("TestJoco", "Joco Test"))
                .block()!!
    }

    @Test
    fun `Get user should return a Joco if user name is Joco`() {
        webTestClient.get()
                .uri("/users/TestJoco")
                .exchange()
                .expectBody<TradingUser>()
                .isEqualTo(tradingUser)
    }

}
