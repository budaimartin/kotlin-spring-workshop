package hcom.mobile.workshop.tradingservice

import hcom.mobile.workshop.tradingservice.controller.UserNotFoundException
import hcom.mobile.workshop.tradingservice.controller.UsersController
import hcom.mobile.workshop.tradingservice.domain.TradingUser
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class UsersControllerTest : BehaviorSpec({
    Given("The UsersController") {
        val mockTradingUserRepository = mockk<TradingUserRepository>()
        val underTest = UsersController(mockTradingUserRepository)
        val testUser = TradingUser("123", "testJoco", "Teszt Jocó")
        every { mockTradingUserRepository.findByUserName("testJoco") } returns Mono.just(testUser)

        When("User exists") {
            val actual = underTest.getUserByUserName("testJoco")
            Then("getUserByUserName should return user") {
                StepVerifier.create(actual)
                        .assertNext { it.shouldBe(testUser) }
                        .verifyComplete()
            }
        }

        every { mockTradingUserRepository.findByUserName("nonexistent") } returns Mono.empty()
        When("User does not exist") {
            val actual = underTest.getUserByUserName("nonexistent")
            Then("getUserByUserName should return error") {
                StepVerifier.create(actual)
                        .expectErrorSatisfies {
                            it.shouldBeInstanceOf<UserNotFoundException>()
                            it.message.shouldContain("nonexistent")
                        }
                        .verify()
                verify { mockTradingUserRepository.findByUserName("nonexistent") }
            }
        }

    }
})